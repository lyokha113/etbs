package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.PublishStatus;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.PublishRequest;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.PublishRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.PublishService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublishServiceImpl implements PublishService {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TemplateRepository templateRepository;

  @Autowired
  private PublishRepository publishRepository;

  @Override
  public List<Publish> getPublishes() {
    return publishRepository.findAll();
  }

  @Override
  public List<Publish> getPublishes(UUID authorId) {
    return publishRepository.getByAuthor_Id(authorId);
  }

  @Override
  public Publish publish(UUID authorId, PublishRequest request) {

    Account author = accountRepository.findById(authorId).orElse(null);
    Publish publish = Publish.builder()
        .content(request.getContent())
        .author(author)
        .status(PublishStatus.PENDING)
        .build();

    return publishRepository.save(publish);
  }

  @Override
  public Publish updatePublish(Integer id, TemplateCreateRequest request) {

    PublishStatus publishStatus = PublishStatus.valueOf(request.getStatus());

    Publish publish = publishRepository.findById(id).orElse(null);
    if (publish == null) {
      throw new BadRequestException("Publish isn't existed");
    }

    publish.setStatus(publishStatus);
    return publishRepository.save(publish);
  }

  @Override
  public Publish checkDuplicate(Publish publish) {
    List<Template> templates = templateRepository.findAll();
    templates = templates.stream().filter(Template::isActive).collect(Collectors.toList());
    Double max = 0.0;
    Template duplicate = null;
    for (Template template : templates) {
      JaroWinklerSimilarity jws = new JaroWinklerSimilarity();
      Double apply = jws.apply(publish.getContent(), template.getContent());

      if (apply > AppConstant.OVER_POINT_CHECK_DUPLICATED) {
        publish.setStatus(PublishStatus.DENIED);
        publish.setDuplicateContent(template.getContent());
        publish.setDuplicateRate(apply);
        return publishRepository.save(publish);
      }

      if (apply > max) {
        max = apply;
        duplicate = template;
      }
    }

    publish.setDuplicateContent(duplicate.getContent());
    publish.setDuplicateRate(max);
    return publishRepository.save(publish);
  }
}
