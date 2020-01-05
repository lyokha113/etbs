package fpt.capstone.etbs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpt.capstone.etbs.model.UserEmail;
import fpt.capstone.etbs.payload.UserEmailRequest;
import java.util.List;
import java.util.UUID;

public interface UserEmailService {

  UserEmail createUserEmail(UUID uuid, UserEmailRequest request) throws JsonProcessingException;

  UserEmail updateUserEmail(Integer id, UserEmailRequest request);

  UserEmail confirmUserEmail(String token);

  void deleteUserEmail(Integer id);

  List<UserEmail> getUserEmailList();
}
