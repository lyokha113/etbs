package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.DraftEmailRequest;
import fpt.capstone.etbs.payload.SendEmailRequest;
import fpt.capstone.etbs.service.EmailService;
import java.security.GeneralSecurityException;
import javax.mail.MessagingException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

  @Autowired
  private EmailService emailSenderService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @PostMapping("/email/draft")
  public ResponseEntity<?> makeDraftEmail(@Valid @RequestBody DraftEmailRequest request)
      throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      emailSenderService.makeDraftEmail(userPrincipal.getId(), request);
      return ResponseEntity.ok(new ApiResponse<>(true, "Draft was made", null));
    } catch (GeneralSecurityException | MessagingException e) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(false, "Invalid login/security information", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PostMapping("/email/send")
  public ResponseEntity<?> sendEmail(
      @Valid @RequestBody SendEmailRequest request) throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      emailSenderService.sendEmail(userPrincipal.getId(), request);
      return ResponseEntity.ok(new ApiResponse<>(true, "Email was sent", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
