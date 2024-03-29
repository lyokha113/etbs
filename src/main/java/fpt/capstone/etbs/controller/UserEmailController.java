package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.component.JwtTokenProvider;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.constant.UserEmailStatus;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    List<UserEmail> userEmails = userEmailService.getUserEmails(userPrincipal.getId());
    List<UserEmailResponse> responses = userEmails.stream().map(UserEmailResponse::setResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", responses));

  }

  @PostMapping("/useremail")
  private ResponseEntity<?> createUserEmail(@Valid @RequestBody StringWrapperRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      UserEmail userEmail = userEmailService
          .createUserEmail(userPrincipal.getId(), request.getString());
      UserEmailResponse response = UserEmailResponse.setResponse(userEmail);

      if (userEmail.getStatus().equals(UserEmailStatus.PENDING)) {
        String token = tokenProvider.generateToken(userEmail.getId());
        emailService.sendConfirmUserEmail(userEmail.getEmail(), token);
      }

      return ResponseEntity.ok(new ApiResponse<>(true, "Email was created", response));
    } catch (BadRequestException | MessagingException | IOException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));

    }
  }

  @DeleteMapping("/useremail/{id}")
  private ResponseEntity<?> deleteUserEmail(@PathVariable("id") Integer id) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      userEmailService.deleteUserEmail(userPrincipal.getId(), id);
      return ResponseEntity.ok(new ApiResponse<>(true, "Email was deleted", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
