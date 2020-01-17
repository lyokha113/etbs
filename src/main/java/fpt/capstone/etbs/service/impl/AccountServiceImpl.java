package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.AuthProvider;
import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Role;
import fpt.capstone.etbs.model.UserEmail;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.AccountRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RoleRepository;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.util.ImageUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
      ;
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

    if (request.getImageUrl() != null) {
      BufferedImage bufferedImage = ImageUtils.base64ToImage(request.getImageUrl());
      String avatar = firebaseService.createUserAvatar(bufferedImage, account.getId().toString());
      account.setImageUrl(avatar);
    }
    if (request.getFullName() != null) {
      account.setFullName(request.getFullName());
    }
    if (request.getPassword() != null) {
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

  private Account setNewAccount(AccountRequest request, AuthProvider provider, String
      avatarURL,
      Role role) {
    Account account = new Account();
    account.setEmail(request.getEmail());
    account.setFullName(request.getFullName());
    account.setActive(true);

    if (!StringUtils.isEmpty(request.getPassword())) {
      account.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    if (role.getId() == RoleEnum.USER.getId()) {

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

      account.setUserEmails(
          Stream.of(UserEmail.builder()
              .account(account)
              .email(request.getEmail())
              .status("Default")
              .build())
              .collect(Collectors.toSet()));
    } else {
      account.setImageUrl(AppConstant.DEFAULT_AVATAR_ADMIN_URL);
    }

    account.setRole(role);
    account.setProvider(provider);
    return accountRepository.save(account);
  }
}
