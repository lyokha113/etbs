package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.UserEmailStatus;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.UserEmail;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.UserEmailRepository;
import fpt.capstone.etbs.service.CustomUserDetailsService;
import fpt.capstone.etbs.service.UserEmailService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserEmailServiceImpl implements UserEmailService {

  @Autowired
  private UserEmailRepository userEmailRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @Override
  public UserEmail createUserEmail(UUID accountId, String email) {

    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    if (account.getEmail().equals(email)) {
      throw new BadRequestException("This is your login email. No need to add.");
    }

    UserEmail userEmail;
    List<UserEmail> userEmails = userEmailRepository.getByAccount_Id(accountId);
    if (userEmails.stream()
        .filter(ue -> ue.getStatus().equals(UserEmailStatus.PENDING)
            || ue.getStatus().equals(UserEmailStatus.APPROVED))
        .count() >= AppConstant.MAX_TEST_EMAIL) {
      throw new BadRequestException(
          "We currently support maximum " + AppConstant.MAX_TEST_EMAIL + " emails for each user");
    }

    Optional<UserEmail> check = userEmails.stream()
        .filter(ue -> ue.getEmail().equals(email)).findAny();

    if (check.isPresent()) {
      userEmail = check.get();

      if (userEmail.getStatus().equals(UserEmailStatus.DELETED)) {
        userEmail.setStatus(UserEmailStatus.APPROVED);
      } else if (userEmail.getStatus().equals(UserEmailStatus.CANCELED)) {
        userEmail.setStatus(UserEmailStatus.PENDING);
      } else {
        throw new BadRequestException("Email is existed in your list");
      }
    } else {
      userEmail = UserEmail.builder()
          .account(account)
          .email(email)
          .status(UserEmailStatus.PENDING)
          .build();
    }

    return userEmailRepository.save(userEmail);
  }

  @Override
  public void confirmUserEmail(Integer id) {
    UserEmail userEmail = userEmailRepository.findById(id).orElse(null);
    if (userEmail == null) {
      throw new BadRequestException("Email doesn't exist");
    }
    if (userEmail.getStatus().equals(UserEmailStatus.CANCELED) ||
        userEmail.getStatus().equals(UserEmailStatus.DELETED)) {
      throw new BadRequestException("Email isn't pending status to approve");
    }

    userEmail.setStatus(UserEmailStatus.APPROVED);
    userEmail = userEmailRepository.save(userEmail);

    messagingTemplate.convertAndSendToUser(userEmail.getAccount().getId().toString(),
            AppConstant.WEB_SOCKET_USER_EMAIL_QUEUE, userEmail);
  }


  @Override
  public void deleteUserEmail(UUID accountId, Integer id) {
    UserEmail userEmail = userEmailRepository.getByAccount_IdAndId(accountId, id).orElse(null);
    if (userEmail == null) {
      throw new BadRequestException("Email doesn't exist");
    }

    userEmail.setStatus(
        userEmail.getStatus().equals(UserEmailStatus.APPROVED)
            ? UserEmailStatus.DELETED
            : UserEmailStatus.CANCELED);
    userEmailRepository.save(userEmail);
  }

  @Override
  public List<UserEmail> getUserEmails(UUID accountId) {
    return userEmailRepository.getByAccount_Id(accountId).stream().filter(
        email -> email.getStatus().equals(UserEmailStatus.PENDING)
            || email.getStatus().equals(UserEmailStatus.APPROVED))
        .collect(Collectors.toList());
  }

  @Override
  public UserEmail getUserEmail(UUID accountId, Integer id) {
    return userEmailRepository.getByAccount_IdAndId(accountId, id).orElse(null);
  }
}
