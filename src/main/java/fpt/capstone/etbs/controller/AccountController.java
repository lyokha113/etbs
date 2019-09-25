package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.CreateAccountRequest;
import fpt.capstone.etbs.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/account")
    public ResponseEntity<ApiResponse> getAccounts(@Valid @RequestBody String email) {
        Account accounts = accountService.getAccountByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "", accounts));
    }

    @PostMapping("/account")
    public ResponseEntity<ApiResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        Account account = accountService.createAccount(request);
        return account != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Account created", account)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Account is existed", null));
    }

    @PutMapping("/account/{id}")
    public ResponseEntity<ApiResponse> updateAccount(@PathVariable("id") UUID uuid,
                                                     @Valid @RequestBody AccountUpdateRequest request) {
        Account account = accountService.updateAccount(uuid, request);
        return account != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Update successful", account)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Update failed. Not found", null));
    }
}
