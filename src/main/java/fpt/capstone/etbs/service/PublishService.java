package fpt.capstone.etbs.service;

import fpt.capstone.etbs.constant.PublishStatus;
import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.payload.PublishRequest;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import java.util.List;
import java.util.UUID;

public interface PublishService {

  List<Publish> getPublishes();
  List<Publish> getPublishes(UUID authorId);
  Publish publish(UUID authorId, PublishRequest request);
  Publish updatePublish(Integer id, TemplateCreateRequest request);
  Publish checkDuplicate(Publish publish);
}
