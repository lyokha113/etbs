package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.AuthProvider;
import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Role;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
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
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends DefaultOAuth2UserService implements AccountService {

  @Autowired
  private AccountRepository accountRepository;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public List<Account> getAccounts() {
    return null;
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

    Account account = new Account();
    account.setEmail(request.getEmail());
    account.setFullName(request.getFullName());
    account.setPassword(passwordEncoder.encode(request.getPassword()));
    account.setImageUrl(AppConstant.DEFAULT_AVATAR_URL);
    account.setActive(true);
    account.setRole(Role.builder().id(RoleEnum.USER.getId()).build());
    account.setProvider(AuthProvider.local);
    account.setWorkspaces(
        Stream.of(Workspace.builder().name(AppConstant.DEFAULT_WORKSPACE_NAME).build())
            .collect(Collectors.toSet()));
    return accountRepository.save(account);
  }

  @Override
  public Account createAccount(RegisterRequest request, int roleId) {
    Account account = getAccountByEmail(request.getEmail());
    if (account == null) {
      account = new Account();
      account.setEmail(request.getEmail());
      account.setFullName(request.getFullName());
      account.setPassword(passwordEncoder.encode(request.getPassword()));
      account.setActive(true);
      if (roleRepository.findById(roleId).isPresent()) {
        account.setRole(roleRepository.findById(roleId).get());
      }
      accountRepository.save(account);
    }
    return account;
  }


  @Override
  public Account updateAccount(UUID uuid, AccountUpdateRequest request) {
    Account account = getAccount(uuid);
    if (account != null) {
      account.setFullName(request.getFullName());
      account.setPassword(passwordEncoder.encode(request.getPassword()));
      accountRepository.save(account);
      return account;
    }
    return null;
  }
}
