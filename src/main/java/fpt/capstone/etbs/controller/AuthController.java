package fpt.capstone.etbs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpt.capstone.etbs.component.JwtTokenProvider;
import fpt.capstone.etbs.exception.DuplicationException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.*;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.util.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

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
    public ResponseEntity<ApiResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
            throws Exception {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Account account = accountService.getAccountByEmail(loginRequest.getEmail());
            LoginResponse response = new LoginResponse(account);
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
    public ResponseEntity<ApiResponse> registerAccount(@Valid @RequestBody RegisterRequest request) throws Exception {
        try {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
            accountService.registerAccount(request);
            Account account = accountService.getAccountByEmail(request.getEmail());
            return account != null ?
                    ResponseEntity.ok(
                            new ApiResponse<>(true, "Account created", account)) :
                    ResponseEntity.badRequest().body(
                            new ApiResponse<>(false, "Account create failed", null));
        } catch (Exception ex) {
            List<String> exceptionMessageChain = ExceptionHandler.getExceptionMessageChain(ex.getCause());
            if (ExceptionHandler.isDuplicateException(exceptionMessageChain)) {
                throw new DuplicationException("Account is existed");
            }
            throw ex;
        }
    }
}

