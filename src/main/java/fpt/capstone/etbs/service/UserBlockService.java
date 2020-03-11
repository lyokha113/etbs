package fpt.capstone.etbs.service;


import fpt.capstone.etbs.model.UserBlock;
import fpt.capstone.etbs.payload.SynchronizeContentRequest;
import fpt.capstone.etbs.payload.UserBlockRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserBlockService {

  List<UserBlock> getUserBlocks(UUID accountId);

  UserBlock getUserBlock(UUID accountId, Integer id);

  UserBlock createUserBlock(UUID accountId, UserBlockRequest request);

  UserBlock updateUserBlock(UUID accountId, Integer id, UserBlockRequest request);

  UserBlock updateUserBlockContent(UUID accountId, Integer id, String content);

  Map<String, List<Integer>> synchronizeContent(UUID accountId, SynchronizeContentRequest request) throws Exception;

  void deleteUserBlock(UUID accountId, Integer id);
}
