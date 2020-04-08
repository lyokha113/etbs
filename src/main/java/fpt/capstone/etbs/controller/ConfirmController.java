package fpt.capstone.etbs.controller;


import fpt.capstone.etbs.component.JwtTokenProvider;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.UserEmailService;
import java.io.IOException;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfirmController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private UserEmailService userEmailService;

  @Autowired
  private JwtTokenProvider tokenProvider;

  @Value("${app.clientConfirmUri}")
  private String clientConfirmUri;

  @GetMapping("/confirm/email")
  private void confirmEmail(@RequestParam("token") String token, HttpServletResponse response) {
    try {
      Integer id = tokenProvider.getTokenValue(token, Integer.class);
      userEmailService.confirmUserEmail(id);
      response.setHeader("Location", clientConfirmUri + "?confirm=email");
      response.setStatus(302);
    } catch (BadRequestException | IOException ex) {
      response.setHeader("Location", clientConfirmUri + "?error=" + ex.getMessage());
    }
  }

  @GetMapping("/confirm/account")
  private void confirmAccount(@RequestParam("token") String token, HttpServletResponse response) {
    try {
      UUID uuid = tokenProvider.getTokenValue(token, UUID.class);
      accountService.confirmAccount(uuid);
      response.setHeader("Location", clientConfirmUri + "?confirm=account");
      response.setStatus(302);
    } catch (BadRequestException | IOException ex) {
      response.setHeader("Location", clientConfirmUri + "?error=" + ex.getMessage());
    }
  }

  @GetMapping("/confirm/recovery")
  private void confirmRecovery(@RequestParam("token") String token, HttpServletResponse response) {
    try {
      UUID uuid = tokenProvider.getTokenValue(token, UUID.class);
      accountService.confirmRecovery(uuid);
      response.setHeader("Location", clientConfirmUri + "?confirm=recovery");
      response.setStatus(302);
    } catch (BadRequestException | IOException | MessagingException ex) {
      response.setHeader("Location", clientConfirmUri + "?error=" + ex.getMessage());
    }
  }
}
