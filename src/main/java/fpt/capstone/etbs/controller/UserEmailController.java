package fpt.capstone.etbs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.component.JwtTokenProvider;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.UserEmail;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.StringWrapperRequest;
import fpt.capstone.etbs.payload.UserEmailResponse;
import fpt.capstone.etbs.service.EmailService;
import fpt.capstone.etbs.service.UserEmailService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserEmailController {

  @Autowired
  private UserEmailService userEmailService;

  @Autowired
  private EmailService emailService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @Autowired
  private JwtTokenProvider tokenProvider;

  @GetMapping("/useremail")
  private ResponseEntity<?> getUserEmails() {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      List<UserEmail> userEmails = userEmailService.getUserEmails(userPrincipal.getId());
      List<UserEmailResponse> responses = userEmails.stream().map(UserEmailResponse::setResponse)
          .collect(Collectors.toList());
      return ResponseEntity.ok(new ApiResponse<>(true, "", responses));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PostMapping("/useremail")
  private ResponseEntity<?> createUserEmail(@Valid @RequestBody StringWrapperRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      UserEmail userEmail = userEmailService
          .createUserEmail(userPrincipal.getId(), request.getString());
      UserEmailResponse response = UserEmailResponse.setResponse(userEmail);

      String token = tokenProvider.generateToken(userEmail);
      emailService.sendConfirmUserEmail(userEmail.getEmail(), token);

      return ResponseEntity.ok(new ApiResponse<>(true, "Email was created", response));
    } catch (BadRequestException | MessagingException | JsonProcessingException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));

    }
  }

  @PatchMapping("/useremail/{id}")
  private ResponseEntity<?> resendConfirmMail(@PathVariable("id") Integer id) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      UserEmail userEmail = userEmailService.getUserEmail(userPrincipal.getId(), id);
      if (userEmail != null) {
        String token = tokenProvider.generateToken(userEmail);
        emailService.sendConfirmUserEmail(userEmail.getEmail(), token);
        return ResponseEntity.ok(new ApiResponse<>(true, "Re-send confirm mail", null));
      }

      return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Wrong information", null));
    } catch (BadRequestException | JsonProcessingException | MessagingException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @DeleteMapping("/useremail/{id}")
  private ResponseEntity<?> deleteUserEmail(@PathVariable("id") Integer id) {
    try {
      userEmailService.deleteUserEmail(id);
      return ResponseEntity.ok(new ApiResponse<>(true, "Email was deleted", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @GetMapping("/useremail/confirm")
  private ResponseEntity<?> confirmUserEmail(@RequestParam("token") String token) {
    try {
      UserEmail userEmail = tokenProvider.getTokenValue(token, UserEmail.class);
      userEmail = userEmailService.confirmUserEmail(userEmail.getId());
      UserEmailResponse response = UserEmailResponse.setResponse(userEmail);
      return ResponseEntity.ok(new ApiResponse<>(true, "Email was approved", response));
    } catch (BadRequestException | IOException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
