package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.UserEmail;
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
  private UserEmailRepository userEmailRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Override
  public UserEmail createUserEmail(UUID accountId, String email) {
    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    UserEmail userEmail = userEmailRepository
        .getByAccount_IdAndEmail(accountId, email).orElse(null);
    if (userEmail != null) {
      throw new BadRequestException("Email is existed");
    }

    userEmail = UserEmail.builder()
        .account(account)
        .email(email)
        .status(false)
        .build();

    return userEmailRepository.save(userEmail);
  }

  @Override
  public UserEmail confirmUserEmail(Integer id) {
    UserEmail userEmail = userEmailRepository.findById(id).orElse(null);
    if (userEmail == null) {
      throw new BadRequestException("Email doesn't exist");
    }
    userEmail.setStatus(true);
    return userEmailRepository.save(userEmail);
  }


  @Override
  public void deleteUserEmail(Integer id) {
    UserEmail userEmail = userEmailRepository.findById(id).orElse(null);
    if (userEmail == null) {
      throw new BadRequestException("Email doesn't exist");
    }
    userEmailRepository.delete(userEmail);
  }

  @Override
  public List<UserEmail> getUserEmails(UUID accountId) {
    return userEmailRepository.getByAccount_Id(accountId);
  }

  @Override
  public UserEmail getUserEmail(UUID accountId, Integer id) {
    return userEmailRepository.getByAccount_IdAndId(accountId, id).orElse(null);
  }
}
