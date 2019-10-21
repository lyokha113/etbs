package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/user")
    public ResponseEntity<ApiResponse> getUserDetail(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Account account = accountService.getAccount(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "", account));
    }

    @PutMapping("/user")
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody AccountUpdateRequest request, Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Account account = accountService.getAccount(userPrincipal.getId());
        account = accountService.updateAccount(account.getId(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Update successful", account));
    }
}
