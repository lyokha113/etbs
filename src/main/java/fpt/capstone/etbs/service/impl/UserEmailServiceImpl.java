package fpt.capstone.etbs.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpt.capstone.etbs.component.JwtTokenProvider;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.UserEmail;
import fpt.capstone.etbs.payload.UserEmailRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.UserEmailRepository;
import fpt.capstone.etbs.service.UserEmailService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserEmailServiceImpl implements UserEmailService {

  @Autowired
  UserEmailRepository userEmailRepository;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  private JwtTokenProvider tokenProvider;

  @Override
  public UserEmail createUserEmail(UUID uuid, UserEmailRequest request)
      throws JsonProcessingException {
    Account account = accountRepository.findById(uuid).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }
    UserEmail userEmail = UserEmail.builder()
        .account(account)
        .email(request.getEmail())
        .status("Requested")
        .build();
    userEmail.setToken(tokenProvider.generateToken(userEmail.getId()));
    return userEmailRepository.save(userEmail);
  }

  @Override
  public UserEmail confirmUserEmail(String token) {
    UserEmail userEmail = userEmailRepository.getByToken(token).orElse(null);
    if (userEmail == null) {
      throw new BadRequestException("User email doesn't exist");
    }
    userEmail.setStatus("Accept");
    return userEmailRepository.save(userEmail);
  }


  @Override
  public void deleteUserEmail(Integer id) {
    UserEmail userEmail = userEmailRepository.findById(id).orElse(null);
    if (userEmail == null) {
      throw new BadRequestException("User email doesn't exist");
    }
    userEmailRepository.delete(userEmail);
  }

  @Override
  public List<UserEmail> getUserEmailList(UUID id) {
    Account account = accountRepository.findById(id).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }
    return userEmailRepository.getAllByAccount(account);
  }
}
