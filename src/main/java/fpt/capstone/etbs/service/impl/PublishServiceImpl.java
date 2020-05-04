package fpt.capstone.etbs.service.impl;

import de.uni_jena.cs.fusion.similarity.jarowinkler.JaroWinklerSimilarity;
import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.PublishStatus;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.ApprovePublishRequest;
import fpt.capstone.etbs.payload.PublishRequest;
import fpt.capstone.etbs.payload.PublishResponse;
import fpt.capstone.etbs.payload.TemplateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.PublishRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.HtmlContentService;
import fpt.capstone.etbs.service.MessagePublisherService;
import fpt.capstone.etbs.service.NotificationService;
import fpt.capstone.etbs.service.PublishService;
import fpt.capstone.etbs.service.TemplateService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
  private HtmlContentService htmlContentService;

  @Autowired
  private MessagePublisherService messagePublisherService;

  @Autowired
  private NotificationService notificationService;

  @Override
  public List<Publish> getPublishes() {
    return publishRepository.findAll();
  }

  @Override
  public List<Publish> getPublishes(UUID authorId) {
    return publishRepository.getByAuthor_Id(authorId);
  }

  @Override
  public List<Publish> getPublishesToRemove() {
    LocalDateTime limit = LocalDateTime.now().minusDays(30);
    List<PublishStatus> statuses = Arrays.asList(PublishStatus.DENIED, PublishStatus.PUBLISHED);
    return publishRepository.getByStatusInAndLastModifiedDateBefore(statuses, limit);
  }

  @Override
  public Publish createPublish(UUID authorId, PublishRequest request) {

    Account author = accountRepository.findById(authorId).orElse(null);
    if (author == null) {
      throw new BadRequestException("Account doesn't exited");
    }

    if (!checkPublishPolicy(authorId)) {
      throw new BadRequestException(
          "Publish request was reached limitation. Please wait for processing and request later.");
    }

    Publish publish = Publish.builder()
        .content(request.getContent())
        .name(request.getName())
        .description(request.getDescription())
        .author(author)
        .status(PublishStatus.PENDING)
        .build();

    publish = publishRepository.save(publish);
    notificationService.createPublishNotification();
    return publish;
  }

  @Override
  public Publish approve(Integer id, ApprovePublishRequest approveRequest) throws Exception {

    Publish publish = publishRepository.findById(id).orElse(null);
    if (publish == null) {
      throw new BadRequestException("Publish isn't existed");
    }

    TemplateRequest request = new TemplateRequest(approveRequest, publish.getContent(),
        publish.getAuthor().getId());
    Template template = templateService.createTemplate(request);

    publish.setStatus(PublishStatus.PUBLISHED);
    publish = publishRepository.save(publish);

    messagePublisherService.sendUpdatePublish(publish.getAuthor().getId().toString(),
        PublishResponse.setResponse(publish));

    notificationService.createApproveNotification(publish.getAuthor(), template.getId());
    return publish;
  }

  @Override
  public Publish deny(Integer id) {
    Publish publish = publishRepository.findById(id).orElse(null);
    if (publish == null) {
      throw new BadRequestException("Publish isn't existed");
    }

    publish.setStatus(PublishStatus.DENIED);
    publish = publishRepository.save(publish);
    messagePublisherService.sendUpdatePublish(publish.getAuthor().getId().toString(),
        PublishResponse.setResponse(publish));

    notificationService.createDenyNotification(publish.getAuthor());
    return publish;
  }

  @Override
  @Async("checkDuplicateAsyncExecutor")
  public void checkDuplicate() {
    List<Publish> publishes = publishRepository.getByStatusEquals(PublishStatus.PENDING);
    checkDuplicate(publishes);

    publishes = getPublishes();
    List<PublishResponse> responses = publishes.stream().map(PublishResponse::setResponse)
        .sorted(Comparator.comparing(PublishResponse::getRequestDate).reversed())
        .collect(Collectors.toList());
    messagePublisherService.sendPublishes(responses);
  }

  @Override
  @Async("checkDuplicateSingleAsyncExecutor")
  public void checkDuplicate(UUID authorId, Publish publish) {
    List<Publish> publishes = Collections.singletonList(publish);
    checkDuplicate(publishes);

    publishes = getPublishes();
    List<PublishResponse> responses = publishes.stream().map(PublishResponse::setResponse)
        .sorted(Comparator.comparing(PublishResponse::getRequestDate).reversed())
        .collect(Collectors.toList());
    messagePublisherService.sendPublishes(responses);

    publishes = publishes.stream().filter(p -> p.getAuthor().getId().equals(authorId))
        .collect(Collectors.toList());
    responses = publishes.stream().map(PublishResponse::setResponse)
        .sorted(Comparator.comparing(PublishResponse::getRequestDate).reversed())
        .collect(Collectors.toList());
    messagePublisherService.sendPublish(authorId.toString(), responses);
  }

  private void checkDuplicate(List<Publish> publishes) {
    List<Template> templates = templateRepository.findAll();

    Map<Template, Double> onlyTextResult, removedTextResult, finalResult;

    Map<String, Template> onlyText = new HashMap<>();
    templates.forEach(t -> {
      onlyText.put(htmlContentService.getOnlyText(t.getContent()), t);
    });

    Map<String, Template> removedText = new HashMap<>();
    templates.forEach(t -> {
      removedText.put(htmlContentService.removeAllText(t.getContent()), t);
    });

    JaroWinklerSimilarity<Template> jws1 = JaroWinklerSimilarity
        .with(onlyText, AppConstant.MIN_DUPLICATION_RATE);
    JaroWinklerSimilarity<Template> jws2 = JaroWinklerSimilarity
        .with(removedText, AppConstant.MIN_DUPLICATION_RATE);

    for (Publish publish : publishes) {
      String content = publish.getContent();
      onlyTextResult = jws1.apply(htmlContentService.getOnlyText(content));
      removedTextResult = jws2.apply(htmlContentService.removeAllText(content));
      finalResult = Stream
          .concat(onlyTextResult.entrySet().stream(), removedTextResult.entrySet().stream())
          .collect(Collectors.toMap(
              Entry::getKey,
              Entry::getValue,
              (v1, v2) -> ((v2 * 3) + v1) / 4,
              HashMap::new));

      Optional<Entry<Template, Double>> maxRate = finalResult.entrySet().stream()
          .max(Entry.comparingByValue());

      if (maxRate.isPresent()) {
        Entry<Template, Double> max = maxRate.get();
        BigDecimal rate = BigDecimal.valueOf(max.getValue() * 100).setScale(3, RoundingMode.DOWN);
        publish.setDuplicateTemplate(max.getKey());
        publish.setDuplicateRate(rate.doubleValue());

        if (max.getValue() >= AppConstant.MAX_DUPLICATION_RATE) {
          publish.setStatus(PublishStatus.DENIED);
        }

      } else {
        publish.setDuplicateTemplate(null);
        publish.setDuplicateRate(0.0);
      }
    }

    publishRepository.saveAll(publishes);
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
}
