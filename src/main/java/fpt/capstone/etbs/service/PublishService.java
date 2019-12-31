package fpt.capstone.etbs.service;

import fpt.capstone.etbs.constant.PublishStatus;
import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.payload.StringWrapperRequest;
import fpt.capstone.etbs.payload.ApprovePublishRequest;
import java.util.List;
import java.util.UUID;

public interface PublishService {

  List<Publish> getPublishes();

  List<Publish> getPublishes(UUID authorId);

  boolean checkPublishPolicy(UUID authorId);

  Publish createPublish(UUID authorId, String content);

  Publish updatePublishStatus(Integer id, PublishStatus status, String name);

  void checkDuplicate(Publish publish);

  void checkDuplicateAsync(Publish publish);

  void checkDuplicate();

  void approve(ApprovePublishRequest approveRequest, Publish publish) throws Exception;
}
