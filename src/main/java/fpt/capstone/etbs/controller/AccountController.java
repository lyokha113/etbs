package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.JwtTokenProvider;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.AccountRequest;
import fpt.capstone.etbs.payload.AccountResponse;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.EmailService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

  @Autowired
  JwtTokenProvider tokenProvider;

  @Autowired
  private AccountService accountService;

  @GetMapping("/account")
  public ResponseEntity<?> getAccounts() {
    List<Account> accounts = accountService.getAccounts();
    List<AccountResponse> response =
        accounts.stream().map(AccountResponse::setResponse).collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @PostMapping("/account")
  public ResponseEntity<?> createAccount(
      @Valid @RequestBody AccountRequest request) {
    try {
      Account account = accountService.createAccount(request);
      AccountResponse response = AccountResponse.setResponse(account);
      return ResponseEntity.ok(new ApiResponse<>(true, "Account created", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/account/confirm")
  public ResponseEntity<?> confirmAccount(@Valid @RequestParam("token") String token) {
    try {
      AccountResponse accountResponse = tokenProvider.getTokenValue(token, AccountResponse.class);
      return ResponseEntity.ok(new ApiResponse<>(true, "", accountResponse));
    } catch (BadRequestException | IOException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/account/{uuid}")
  public ResponseEntity<?> updateAccount(
      @PathVariable("uuid") UUID id,
      @Valid @RequestBody AccountRequest request) {
    try {
      Account account = accountService.updateAccount(id, request);
      AccountResponse response = AccountResponse.setResponse(account);
      return ResponseEntity.ok(new ApiResponse<>(true, "Account updated", response));
    } catch (BadRequestException | IOException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PatchMapping("/account/{uuid}")
  public ResponseEntity<?> updateAccountStatus(
      @PathVariable("uuid") UUID id,
      @RequestParam("active") boolean active) {
    try {
      Account account = accountService.updateAccountStatus(id, active);
      AccountResponse response = AccountResponse.setResponse(account);
      return ResponseEntity.ok(new ApiResponse<>(true, "Account updated", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

}
