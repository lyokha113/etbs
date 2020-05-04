package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.component.JwtTokenProvider;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.constant.AuthProvider;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.DraftEmailRequest;
import fpt.capstone.etbs.payload.SendEmailRequest;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.EmailService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private EmailService emailService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @Autowired
  private JwtTokenProvider tokenProvider;

  @Value("${app.clientGoogleAuthUri}")
  private String clientGoogleUri;

  @PostMapping("/email/draft/yahoo")
  public ResponseEntity<?> makeDraftYahoo(@Valid @RequestBody DraftEmailRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      emailService.makeDraftYahoo(userPrincipal.getId(), request);
      return ResponseEntity.ok(new ApiResponse<>(true, "Draft was made", null));
    } catch (MessagingException e) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(false, "Invalid login/security information", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PostMapping("/email/draft/outlook")
  public ResponseEntity<?> makeDraftOutlook(@Valid @RequestBody DraftEmailRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      emailService.makeDraftOutlook(userPrincipal.getId(), request);
      return ResponseEntity.ok(new ApiResponse<>(true, "Draft was made", null));
    } catch (MessagingException e) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(false, "Invalid login/security information", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @GetMapping("/email/draft/gmail")
  public void makeDraftGMail(@RequestParam String state, @RequestParam String code,
      HttpServletResponse response) throws IOException {
    try {
      emailService.makeDraftGMail(state, code);
      response.setHeader("Location", clientGoogleUri);
      response.setStatus(302);
    } catch (MessagingException | GeneralSecurityException e) {
      response.setHeader("Location", clientGoogleUri + "?error=Google services error");
    } catch (BadRequestException ex) {
      response.setHeader("Location", clientGoogleUri + "?error=" + ex.getMessage());
    }
  }

  @PostMapping("/email/send")
  public ResponseEntity<?> sendEmail(
      @Valid @RequestBody SendEmailRequest request) throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      emailService.sendEmail(userPrincipal.getId(), request);
      return ResponseEntity.ok(new ApiResponse<>(true, "Email was sent", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PostMapping("/email/confirm")
  public ResponseEntity<?> sendConfirmAccount(@Valid @RequestParam String email) {
    try {
      Account account = accountService.getAccountByEmail(email);

      if (account == null) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Account isn't existed", null));
      }

      if (account.getProvider().equals(AuthProvider.google)) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "This email is registered with Google", null));
      }

      String token = tokenProvider.generateToken(account.getId());
      if (!account.isApproved()) {
        emailService.sendConfirmAccount(email, token);
      } else {
        emailService.sendConfirmRecovery(email, token);
      }
      return ResponseEntity.ok(new ApiResponse<>(true, "Email was sent", null));
    } catch (BadRequestException | MessagingException | IOException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

}
