package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.payload.ApprovePublishRequest;
import fpt.capstone.etbs.payload.PublishRequest;
import java.util.List;
import java.util.UUID;

public interface PublishService {

  List<Publish> getPublishes();

  List<Publish> getPublishes(UUID authorId);

  Publish createPublish(UUID authorId, PublishRequest request);

  Publish approve(Integer id, ApprovePublishRequest approveRequest) throws Exception;

  Publish deny(Integer id);

  void checkDuplicate(UUID authorId, Publish publish);

  void checkDuplicate();
}
