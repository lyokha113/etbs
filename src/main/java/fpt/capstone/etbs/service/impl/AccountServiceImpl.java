package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.AuthProvider;
import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Role;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.AccountCreateRequest;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
import fpt.capstone.etbs.payload.AuthResponse;
import fpt.capstone.etbs.payload.RegisterRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RoleRepository;
import fpt.capstone.etbs.service.AccountService;
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
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

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
  public Account registerAccount(RegisterRequest request) {

    if (getAccountByEmail(request.getEmail()) != null) {
      throw new BadRequestException("Email is existed");
    }

    Role role = Role.builder()
        .id(RoleEnum.USER.getId())
        .name(RoleEnum.USER.toString())
        .build();

    return setNewAccount(request, AuthProvider.local,null, role);
  }

  @Override
  public Account createAccount(AccountCreateRequest request) {

    if (getAccountByEmail(request.getEmail()) != null) {
      throw new BadRequestException("Email is existed");
    }

    Role role = roleRepository.findById(request.getRoleId()).orElse(null);
    if (role == null) {
      throw new BadRequestException("Role doesn't exist");
    }

    RegisterRequest rr = RegisterRequest.builder()
        .email(request.getEmail())
        .fullName(request.getFullName())
        .password(request.getPassword())
        .build();

    return setNewAccount(rr, AuthProvider.local,null, role);
  }

  @Override
  public Account createGoogleAccount(String email, String name, String avatarURL) {
    RegisterRequest rr = RegisterRequest.builder()
        .email(email)
        .fullName(name)
        .build();

    Role role = Role.builder()
        .id(RoleEnum.USER.getId())
        .name(RoleEnum.USER.toString())
        .build();

    return setNewAccount(rr, AuthProvider.google,avatarURL, role);
  }

  @Override
  public Account updateAccount(UUID uuid, AccountUpdateRequest request) {

    Account account = getAccount(uuid);
    if (account == null) {
      throw new BadRequestException("Account doesn't existed");
    }

    if (account.getProvider().equals(AuthProvider.google)) {
      throw new BadRequestException("Google account can't be update");
    }

    account.setFullName(request.getFullName());
    if (!StringUtils.isEmpty(request.getPassword())) {
      account.setPassword(passwordEncoder.encode(request.getPassword()));
    }
    return accountRepository.save(account);
  }

  @Override
  public Account updateAccount(UUID uuid, Boolean active) {
    Account account = getAccount(uuid);
    if (account == null) {
      throw new BadRequestException("Account doesn't existed");
    }
    account.setActive(active);
    return accountRepository.save(account);
  }

  @Override
  public Account updateGoogleAccount(Account acccount, String name, String avatarURL) {
    acccount.setFullName(name);
    acccount.setImageUrl(avatarURL);
    return accountRepository.save(acccount);
  }

  private Account setNewAccount(RegisterRequest request, AuthProvider provider, String avatarURL, Role role) {
    Account account = new Account();
    account.setEmail(request.getEmail());
    account.setFullName(request.getFullName());
    account.setProvider(provider);
    account.setActive(true);
    account.setRole(role);

    if (!StringUtils.isEmpty(request.getPassword())) {
      account.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    if (!StringUtils.isEmpty(avatarURL)) {
      account.setImageUrl(AppConstant.DEFAULT_AVATAR_URL);
    }

    if (role.getId() == RoleEnum.USER.getId()) {
      account.setWorkspaces(
          Stream.of(Workspace.builder()
              .name(AppConstant.DEFAULT_WORKSPACE_NAME)
              .account(account)
              .build())
              .collect(Collectors.toSet()));
    }

    return accountRepository.save(account);
  }
}
