package fpt.capstone.etbs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpt.capstone.etbs.model.UserEmail;
import java.util.List;
import java.util.UUID;

public interface UserEmailService {

  List<UserEmail> getUserEmails(UUID accountId);

  UserEmail getUserEmail(UUID accountId, Integer id);

  UserEmail createUserEmail(UUID accountId, String email) throws JsonProcessingException;

  void confirmUserEmail(Integer id);

  void deleteUserEmail(UUID accountId, Integer id);
}
