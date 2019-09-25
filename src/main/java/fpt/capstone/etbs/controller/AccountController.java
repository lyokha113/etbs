package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.RegisterRequest;
import fpt.capstone.etbs.service.AccountService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
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
        return ResponseEntity.ok(new ApiResponse<>(1, accounts));
    }

    @PostMapping("/account")
    public ResponseEntity<ApiResponse> createAccount(@Valid @RequestBody RegisterRequest request) {
        try {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
            accountService.registerAccount(request);
            Account account = accountService.getAccountByEmail(request.getEmail());
            return account != null ? ResponseEntity.ok(new ApiResponse<>(1, account)) :
                    ResponseEntity.ok(new ApiResponse<>(0, "Account Created Failed"));
        } catch (Exception ex) {
            return ResponseEntity.ok(new ApiResponse<>(-1, ex.getMessage()));
        }
    }

    @PutMapping("/account/{id}")
    public ResponseEntity<ApiResponse> updateAccount(@PathVariable("id") UUID uuid,
                                                     @Valid @RequestBody AccountUpdateRequest request) {
        try {
            if (accountService.updateAccount(uuid, request)) {
                return ResponseEntity.ok(new ApiResponse<>(1, "Update Successful"));
            } else {
                return ResponseEntity.ok(new ApiResponse<>(0, "Update Failed. Not found"));
            }
        } catch (Exception ex) {
            return ResponseEntity.ok(new ApiResponse<>(-1, ex.getMessage()));
        }
    }

}
