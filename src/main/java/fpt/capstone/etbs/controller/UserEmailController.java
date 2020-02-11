package fpt.capstone.etbs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.UserEmail;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.SendConfirmEmailRequest;
import fpt.capstone.etbs.payload.UserEmailRequest;
import fpt.capstone.etbs.payload.UserEmailResponse;
import fpt.capstone.etbs.service.EmailService;
import fpt.capstone.etbs.service.UserEmailService;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @GetMapping("/useremail")
  private ResponseEntity<?> getUserEmails() {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      List<UserEmail> userEmails = userEmailService.getUserEmailList(userPrincipal.getId());
      List<UserEmailResponse> responses = userEmails.stream().map(UserEmailResponse::setResponse)
          .collect(Collectors.toList());
      return ResponseEntity.ok(new ApiResponse<>(true, "", responses));
    } catch (BadRequestException bre) {
      return ResponseEntity.ok(new ApiResponse<>(false, "", null));
    }
  }

  @PostMapping("/useremail")
  private ResponseEntity<?> addUserEmail(
      @Valid @RequestBody UserEmailRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      UserEmail userEmail = userEmailService.createUserEmail(userPrincipal.getId(), request);
      SendConfirmEmailRequest emailRequest = SendConfirmEmailRequest.builder()
          .email(userEmail.getEmail())
          .token(userEmail.getToken())
          .build();
      emailService.sendConfirmUserEmail(emailRequest);
      UserEmailResponse response = UserEmailResponse.setResponse(userEmail);
      return ResponseEntity.ok(new ApiResponse<>(true, "", response));
    } catch (BadRequestException | MessagingException | JsonProcessingException e) {
      return ResponseEntity.ok(new ApiResponse<>(false, "Can not send email", null));
    }
  }

  @DeleteMapping("/useremail/{id}")
  private ResponseEntity<?> deleteUserEmail(
      @PathVariable("id") Integer id) {
    try {
      userEmailService.deleteUserEmail(id);
      return ResponseEntity.ok(new ApiResponse<>(true, "", null));
    } catch (BadRequestException bre) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/useremail/confirm")
  private ResponseEntity<?> confirmUserEmail(
      @RequestParam("token") String token) {
    try {
      UserEmail userEmail = userEmailService.confirmUserEmail(token);
      UserEmailResponse response = UserEmailResponse.setResponse(userEmail);
      return ResponseEntity.ok(new ApiResponse<>(true, "", response));
    } catch (BadRequestException bre) {
      return ResponseEntity.badRequest().build();
    }
  }
}
