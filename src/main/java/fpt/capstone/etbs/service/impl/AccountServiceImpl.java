package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.AuthProvider;
import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Role;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.AccountRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.EmailService;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.util.ImageUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AccountServiceImpl implements AccountService {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private FirebaseService firebaseService;

  @Autowired
  private EmailService emailService;

  @Override
  public List<Account> getAccounts() {
    return accountRepository.findAll();
  }

  @Override
  public Account getAccount(UUID uuid) {
    return accountRepository.findById(uuid).orElse(null);
  }

  @Override
  public Account getAccountByEmail(String email) {
    return accountRepository.getByEmail(email).orElse(null);
  }

  @Override
  public Account registerAccount(AccountRequest request) {

    if (getAccountByEmail(request.getEmail()) != null) {
      throw new BadRequestException("Email is existed");
    }

    Role role = Role.builder()
        .id(RoleEnum.USER.getId())
        .name(RoleEnum.USER.toString())
        .build();

    return setNewAccount(request, AuthProvider.local, null, role);
  }

  @Override
  public Account createAccount(AccountRequest request) {

    if (getAccountByEmail(request.getEmail()) != null) {
      throw new BadRequestException("Email is existed");
    }

    Role role = Role.builder()
        .id(RoleEnum.ADMINISTRATOR.getId())
        .name(RoleEnum.ADMINISTRATOR.toString())
        .build();

    return setNewAccount(request, AuthProvider.local, null, role);
  }

  @Override
  public Account updateAccount(UUID uuid, AccountRequest request) throws IOException {

    Account account = getAccount(uuid);
    if (account == null) {
      throw new BadRequestException("Account doesn't existed");
    }

    if (!StringUtils.isEmpty(request.getPassword())) {
      account.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    account.setFullName(request.getFullName());
    return accountRepository.save(account);
  }

  @Override
  public Account updateProfile(UUID uuid, AccountRequest request) throws IOException {

    Account account = getAccount(uuid);
    if (account == null) {
      throw new BadRequestException("Account doesn't existed");
    }

    if (account.getProvider().equals(AuthProvider.google)) {
      throw new BadRequestException("Google account can't be update");
    }

    if (!StringUtils.isEmpty(request.getImageUrl())) {
      BufferedImage bufferedImage = ImageUtils.base64ToImage(request.getImageUrl());
      String avatar = firebaseService.createUserAvatar(bufferedImage, account.getId().toString());
      account.setImageUrl(avatar);
    }
    if (!StringUtils.isEmpty(request.getFullName())) {
      account.setFullName(request.getFullName());
    }
    if (!StringUtils.isEmpty(request.getPassword())) {
      account.setPassword(passwordEncoder.encode(request.getPassword()));
    }
    return accountRepository.save(account);
  }

  @Override
  public Account updateAccountStatus(UUID uuid, Boolean active) {
    Account account = getAccount(uuid);
    if (account == null) {
      throw new BadRequestException("Account doesn't existed");
    }
    account.setActive(active);
    return accountRepository.save(account);
  }

  @Override
  public Account setGoogleAccount(String email, String name, String avatar) {
    Account account = getAccountByEmail(email);
    if (account == null) {

      AccountRequest accountRequest = AccountRequest.builder()
          .email(email)
          .fullName(name)
          .build();

      Role role = Role.builder()
          .id(RoleEnum.USER.getId())
          .name(RoleEnum.USER.toString())
          .build();

      return setNewAccount(accountRequest, AuthProvider.google, avatar, role);

    }

    if (!account.isActive()) {
      throw new BadRequestException("This account was locked");
    }

    if (account.getProvider().equals(AuthProvider.google)) {
      account.setFullName(name);
      account.setImageUrl(avatar);
      return accountRepository.save(account);
    }

    throw new BadRequestException(
        "This email was registered in system. Please use email and password to login");

  }

  @Override
  public void confirmAccount(UUID uuid) {
    Account account = getAccount(uuid);
    if (account == null) {
      throw new BadRequestException("Account doesn't existed");
    }

    account.setApproved(true);
    accountRepository.save(account);
  }

  @Override
  public void confirmRecovery(UUID uuid) throws IOException, MessagingException {
    Account account = getAccount(uuid);
    if (account == null) {
      throw new BadRequestException("Account doesn't existed");
    }

    if (account.getProvider().equals(AuthProvider.google)) {
      throw new BadRequestException("Can't recover password for Google account");
    }

    String password = fpt.capstone.etbs.util.StringUtils.generateRandomString(8);
    String encode = passwordEncoder.encode(password);
    account.setPassword(encode);
    accountRepository.save(account);
    emailService.sendRecovery(account.getEmail(), password);
  }

  private Account setNewAccount(AccountRequest request, AuthProvider provider, String avatarURL,
      Role role) {
    Account account = new Account();
    account.setEmail(request.getEmail());
    account.setFullName(request.getFullName());
    account.setActive(true);

    if (!StringUtils.isEmpty(request.getPassword())) {
      account.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    if (role.getId() == RoleEnum.USER.getId()) {

      if (provider.equals(AuthProvider.google)) {
        account.setApproved(true);
      }

      if (!StringUtils.isEmpty(avatarURL)) {
        account.setImageUrl(avatarURL);
      } else {
        account.setImageUrl(AppConstant.DEFAULT_AVATAR_URL);
      }

      account.setWorkspaces(
          Stream.of(Workspace.builder()
              .name(AppConstant.DEFAULT_WORKSPACE_NAME)
              .account(account)
              .build())
              .collect(Collectors.toSet()));

    } else {
      account.setApproved(true);
      account.setImageUrl(AppConstant.DEFAULT_AVATAR_ADMIN_URL);
    }

    account.setRole(role);
    account.setProvider(provider);
    return accountRepository.save(account);
  }
}
