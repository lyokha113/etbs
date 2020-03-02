package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.PublishStatus;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.ApprovePublishRequest;
import fpt.capstone.etbs.payload.PublishResponse;
import fpt.capstone.etbs.payload.TemplateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.PublishRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.PublishService;
import fpt.capstone.etbs.service.TemplateService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishServiceImpl implements PublishService {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TemplateRepository templateRepository;

  @Autowired
  private PublishRepository publishRepository;

  @Autowired
  private TemplateService templateService;

  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @Override
  public List<Publish> getPublishes() {
    return publishRepository.findAll();
  }

  @Override
  public List<Publish> getPublishes(UUID authorId) {
    return publishRepository.getByAuthor_Id(authorId);
  }

  @Override
  public Publish createPublish(UUID authorId, String content) {

    Account author = accountRepository.findById(authorId).orElse(null);
    if (author == null) {
      throw new BadRequestException("Account doesn't exited");
    }

    if (StringUtils.isBlank(content)) {
      throw new BadRequestException("Content mustn't blank");
    }

    if (!checkPublishPolicy(authorId)) {
      throw new BadRequestException(
          "Publish request was reached limitation. Please wait for processing and request later.");
    }

    Publish publish = Publish.builder()
        .content(content)
        .author(author)
        .status(PublishStatus.PENDING)
        .build();

    return publishRepository.save(publish);
  }

  @Override
  public Publish updatePublishStatus(Integer id, PublishStatus status, String name) {

    if (status.equals(PublishStatus.PROCESSING)) {
      Template template = templateRepository.getByName(name).orElse(null);
      if (template != null) {
        throw new BadRequestException("Template name is existed");
      }
    }

    Publish publish = publishRepository.findById(id).orElse(null);
    if (publish == null) {
      throw new BadRequestException("Publish isn't existed");
    }

    publish.setStatus(status);
    publish = publishRepository.save(publish);
    sendMessageWS();
    return publish;
  }

  @Override
  @Async("checkDuplicateAsyncExecutor")
  public void checkDuplicateAsync(Publish publish) {
    checkDuplicate(publish);
    sendMessageWS();
  }

  @Override
  @Async("approvePublishAsyncExecutor")
  @Transactional
  public void approve(ApprovePublishRequest approveRequest, Publish publish) {
    try {

      TemplateRequest request = new TemplateRequest(approveRequest, publish.getContent(),
          publish.getAuthor().getId());
      Template template = templateService.createTemplate(request);
      template = templateService.updateContentImage(template);
      templateService.updateThumbnail(template);
      publish.setStatus(PublishStatus.PUBLISHED);
      publishRepository.save(publish);

      checkDuplicate();
      sendMessageWS();
    } catch (Exception e) {
      publish.setStatus(PublishStatus.ERROR);
      publishRepository.save(publish);
    }
  }

  private boolean checkPublishPolicy(UUID authorId) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startDay = now.toLocalDate().atTime(LocalTime.MIN);
    LocalDateTime endDay = now.toLocalDate().atTime(LocalTime.MAX);
    List<PublishStatus> statuses = Arrays
        .asList(PublishStatus.PENDING, PublishStatus.PROCESSING, PublishStatus.DENIED);
    long count = publishRepository.countByAuthor_IdAndStatusInAndCreatedDateBetween(
        authorId, statuses, startDay, endDay);
    return count < AppConstant.MAX_PENDING_PUBLISH;
  }

  private void checkDuplicate() {
    List<Publish> publishes = publishRepository.getByStatusEquals(PublishStatus.PENDING);
    for (Publish publish : publishes) {
      checkDuplicate(publish);
    }
  }

  private void checkDuplicate(Publish publish) {
    List<Template> templates = templateRepository.findAll();
    double maxRate = AppConstant.MIN_DUPLICATION_RATE;
    Template duplicate = null;
    for (Template template : templates) {
      JaroWinklerSimilarity jws = new JaroWinklerSimilarity();
      Double duplicationRate = jws.apply(publish.getContent(), template.getContent());

      if (duplicationRate >= AppConstant.MAX_DUPLICATION_RATE) {
        BigDecimal rate = new BigDecimal(duplicationRate * 100).setScale(3, RoundingMode.DOWN);
        publish.setStatus(PublishStatus.DENIED);
        publish.setDuplicateTemplate(duplicate);
        publish.setDuplicateRate(rate.doubleValue());
        publishRepository.save(publish);
      }

      if (duplicationRate > maxRate) {
        maxRate = duplicationRate;
        duplicate = template;
      }
    }

    if (duplicate != null) {
      BigDecimal rate = new BigDecimal(maxRate * 100).setScale(3, RoundingMode.DOWN);
      publish.setDuplicateRate(rate.doubleValue());
    } else {
      publish.setDuplicateRate(0.0);
    }

    publish.setDuplicateTemplate(duplicate);
    publishRepository.save(publish);
  }

  private void sendMessageWS() {
    List<Publish> publishes = getPublishes();
    List<PublishResponse> responses = publishes.stream().map(PublishResponse::setResponse)
        .sorted(Comparator.comparing(PublishResponse::getRequestDate).reversed())
        .collect(Collectors.toList());
    messagingTemplate.convertAndSend(AppConstant.WEB_SOCKET_PUBLISH_TOPIC, responses);
  }
}
