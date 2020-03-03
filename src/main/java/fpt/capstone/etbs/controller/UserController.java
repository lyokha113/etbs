package fpt.capstone.etbs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.component.JwtTokenProvider;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.AccountRequest;
import fpt.capstone.etbs.payload.AccountResponse;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.LoginRequest;
import fpt.capstone.etbs.payload.LoginResponse;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.EmailService;
import java.io.IOException;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @Autowired
  private JwtTokenProvider tokenProvider;

  @Autowired
  private EmailService emailService;

  @GetMapping("/user")
  public ResponseEntity<?> getUserDetail() {
    try {
      Authentication auth = authenticationFacade.getAuthentication();
      UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
      Account account = accountService.getAccount(userPrincipal.getId());
      AccountResponse response = AccountResponse.setResponse(account);
      return ResponseEntity.ok(new ApiResponse<>(true, "Account details get successful", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/user")
  public ResponseEntity<?> updateUser(
      @Valid @RequestBody AccountRequest request) {
    try {
      Authentication auth = authenticationFacade.getAuthentication();
      UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
      Account account = accountService.updateProfile(userPrincipal.getId(), request);
      AccountResponse response = AccountResponse.setResponse(account);
      return ResponseEntity.ok(new ApiResponse<>(true, "Account updated", response));
    } catch (BadRequestException | IOException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/user/invite")
  public ResponseEntity<?> updateUserInvite(@Valid @RequestParam("allow") boolean allow) {
    try {
      Authentication auth = authenticationFacade.getAuthentication();
      UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
      Account account = accountService.updateInvite(userPrincipal.getId(), allow);
      AccountResponse response = AccountResponse.setResponse(account);
      return ResponseEntity.ok(new ApiResponse<>(true, "Account updated", response));
    } catch (BadRequestException | IOException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest)
      throws Exception {
    try {

      if (StringUtils.isEmpty(loginRequest.getEmail()) || StringUtils
          .isEmpty(loginRequest.getPassword())) {
        throw new BadCredentialsException("");
      }

      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(), loginRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      Account account = accountService.getAccountByEmail(loginRequest.getEmail());
      AccountResponse response = AccountResponse.setResponse(account);
      String jwt = tokenProvider.generateToken(response);
      return ResponseEntity.ok(
          new ApiResponse<>(true, "Logged successfully", new LoginResponse(jwt)));
    } catch (BadCredentialsException ex) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(false, "Incorrect login information", null));
    } catch (LockedException ex) {
      return ResponseEntity.badRequest()
          .body((new ApiResponse<>(false, "Account was locked or haven't approved.", null)));
    } catch (JsonProcessingException e) {
      throw new Exception("Json parsing error");
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerAccount(@Valid @RequestBody AccountRequest request) {
    try {
      Account account = accountService.registerAccount(request);
      String token = tokenProvider.generateToken(account.getId());
      emailService.sendConfirmAccount(account.getEmail(), token);
      return ResponseEntity.ok(
          new ApiResponse<>(true, "Account created", AccountResponse.setResponse(account)));
    } catch (BadRequestException | MessagingException | IOException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }


}
