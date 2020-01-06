package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.AuthProvider;
import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Role;
import fpt.capstone.etbs.model.UserEmail;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.AccountCreateRequest;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
import fpt.capstone.etbs.payload.RegisterRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RoleRepository;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.FirebaseService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AccountServiceImpl extends DefaultOAuth2UserService implements AccountService {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private RoleRepository roleRepository;

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
  public Account registerAccount(RegisterRequest request) {

    if (getAccountByEmail(request.getEmail()) != null) {
      throw new BadRequestException("Email is existed");
    }

    return setNewAccount(request, Role.builder()
        .id(RoleEnum.USER.getId())
        .name(RoleEnum.USER.toString())
        .build());
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

    return setNewAccount(
        RegisterRequest.builder()
            .email(request.getEmail())
            .fullName(request.getFullName())
            .password(request.getPassword())
            .build(),
        role);
  }

  @Override
  public Account updateAccount(UUID uuid, AccountUpdateRequest request) throws IOException {

    Account account = getAccount(uuid);
    if (account == null) {
      throw new BadRequestException("Account doesn't existed");
    }

    if (account.getProvider().equals(AuthProvider.google)) {
      throw new BadRequestException("Google account can't be updated");
    }
    if (request.getFullName() != null) {
      if (!request.getFullName().equals(account.getFullName())) {
        account.setFullName(request.getFullName());
      }
    }
    if (request.getActive() != null) {
      account.setActive(request.getActive());
    }
    if (!StringUtils.isEmpty(request.getPassword())) {
      account.setPassword(passwordEncoder.encode(request.getPassword()));
    }
    if (request.getImageUrl() != null) {
      if (!request.getImageUrl().equals(account.getImageUrl())) {
        String base64Image = request.getImageUrl().split(",")[1];
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        account
            .setImageUrl(
                firebaseService.updateUserImage(bufferedImage, account.getId().toString()));
      }
    }
    return accountRepository.save(account);
  }

  private Account setNewAccount(RegisterRequest request, Role role) {
    Account account = new Account();
    account.setEmail(request.getEmail());
    account.setFullName(request.getFullName());
    account.setPassword(passwordEncoder.encode(request.getPassword()));
    account.setImageUrl(AppConstant.DEFAULT_AVATAR_URL);
    account.setActive(true);
    account.setRole(role);
    account.setProvider(AuthProvider.local);
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
            .name(request.getFullName())
            .provider("Gmail")
            .status("Default")
            .build())
            .collect(Collectors.toSet()));
    return accountRepository.save(account);
  }
}
