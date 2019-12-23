package fpt.capstone.etbs.service;

import fpt.capstone.etbs.constant.PublishStatus;
import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.payload.ApprovePublishRequest;
import fpt.capstone.etbs.payload.PublishRequest;
import java.util.List;
import java.util.UUID;

public interface PublishService {

  List<Publish> getPublishes();

  List<Publish> getPublishes(UUID authorId);

  Publish createPublish(UUID authorId, PublishRequest request);

  Publish updatePublishStatus(Integer id, PublishStatus status, String name);

  Publish checkDuplicate(Publish publish);

  void checkDuplicate();

  void approve(ApprovePublishRequest approveRequest, Publish publish) throws Exception;
}
