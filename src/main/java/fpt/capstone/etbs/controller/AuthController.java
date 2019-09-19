package fpt.capstone.etbs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpt.capstone.etbs.component.JwtTokenProvider;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Account account = accountService.getAccountByEmail(loginRequest.getEmail());
            LoginResponse response = new LoginResponse(account);
            String jwt = tokenProvider.generateToken(response);
            return ResponseEntity.ok(new ApiResponse<>(1, new JwtAuthenticationResponse(jwt)));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.ok(new ApiResponse<>(0, "incorrect login"));
        } catch (LockedException ex) {
            return ResponseEntity.ok(new ApiResponse<>(0, "account was locked"));
        } catch (JsonProcessingException e) {
            return ResponseEntity.ok(new ApiResponse<>(-1, "json parsing error"));
        }

    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerAccount(@Valid @RequestBody RegisterRequest request) {
        try {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
            accountService.registerAccount(request);
            Account account = accountService.getAccountByEmail(request.getEmail());
            return account != null ? ResponseEntity.ok(new ApiResponse<>(1, "account created")) :
                    ResponseEntity.ok(new ApiResponse<>(0, "account create failed"));
        } catch (Exception ex) {
            if (ex.getMessage().contains("ConstraintViolationException")) {
                return ResponseEntity.ok(new ApiResponse<>(0, "account is existed"));
            }
            return ResponseEntity.ok(new ApiResponse<>(-1, ex.getMessage()));
        }
    }
}

