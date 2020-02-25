package fpt.capstone.etbs.service;


import fpt.capstone.etbs.model.UserBlock;
import fpt.capstone.etbs.payload.UserBlockRequest;
import java.util.List;
import java.util.UUID;

public interface UserBlockService {

  List<UserBlock> getUserBlocks(UUID accountId);

  UserBlock createUserBlock(UUID accountId, UserBlockRequest request);

  UserBlock updateUserBlock(UUID accountId, Integer id, UserBlockRequest request);

  UserBlock updateUserBlockContent(UUID accountId, Integer id, String content);

  void deleteUserBlock(UUID accountId, Integer id);

  void synchronizeToRawTemplates(Integer id);
}
