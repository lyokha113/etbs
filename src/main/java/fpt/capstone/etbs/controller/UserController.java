package fpt.capstone.etbs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpt.capstone.etbs.component.JwtTokenProvider;
import fpt.capstone.etbs.constant.AuthProvider;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.*;
import fpt.capstone.etbs.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @GetMapping("/user")
    public ResponseEntity<ApiResponse> getUserDetail(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Account account = accountService.getAccount(userPrincipal.getId());
        AccountResponse response = AccountResponse.setResponse(account);
        return ResponseEntity.ok(new ApiResponse<>(true, "", response));
    }

    @PutMapping("/user")
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody AccountUpdateRequest request, Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Account account = accountService.getAccount(userPrincipal.getId());
        account = accountService.updateAccount(account.getId(), request);
        AccountResponse response = AccountResponse.setResponse(account);
        return ResponseEntity.ok(new ApiResponse<>(true, "Update successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
            throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Account account = accountService.getAccountByEmail(loginRequest.getEmail());

            if (account.getProvider().equals(AuthProvider.google)) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Your account wasn't create with local", null));
            }

            AccountResponse response = AccountResponse.setResponse(account);
            String jwt = tokenProvider.generateToken(response);
            return ResponseEntity.ok(new ApiResponse<>(true, "Logged successfully",
                    new JwtAuthenticationResponse(jwt)));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Incorrect login", null));
        } catch (LockedException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Account was locked", null));
        } catch (JsonProcessingException e) {
            throw new Exception("Json parsing error");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerAccount(@Valid @RequestBody RegisterRequest request) {
        try {
            Account account = accountService.registerAccount(request);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Account created", AccountResponse.setResponse(account)));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }
}
