package fpt.capstone.etbs.controller;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.services.gmail.Gmail;
import fpt.capstone.etbs.component.GoogleAuthenticator;
import fpt.capstone.etbs.component.JwtTokenProvider;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.AccountResponse;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.LoginResponse;
import fpt.capstone.etbs.payload.StringWrapperRequest;
import fpt.capstone.etbs.service.AccountService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private JwtTokenProvider tokenProvider;

  @Autowired
  private GoogleIdTokenVerifier googleIdTokenVerifier;

  @Autowired
  private GoogleAuthenticator googleAuthenticator;

  @GetMapping("/google/authorize")
  public ResponseEntity<?> getAuthorizedURL() throws Exception {
    String authorize = googleAuthenticator.authorize();
    return ResponseEntity.ok(new ApiResponse<>(true, "Authorized url", authorize));
  }

  @GetMapping("/google/authorize/verify")
  public ResponseEntity<?> verifyAuthorizeURL(@RequestParam String code)
      throws GeneralSecurityException, IOException {
    Gmail gmail = googleAuthenticator.getGMailInstance(code);
    return ResponseEntity.ok(new ApiResponse<>(true, "Code", null));
  }

  @PostMapping("/google/login")
  public ResponseEntity<?> loginByGoogle(@Valid @RequestBody StringWrapperRequest wrapper) {
    try {

      String token = wrapper.getString();
      if (StringUtils.isEmpty(token)) {
        throw new BadCredentialsException("Invalid login information");
      }

      GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(token);
      if (googleIdToken == null) {
        throw new BadRequestException(
            "Google token error");
      }

      Payload payload = googleIdToken.getPayload();
      String email = payload.getEmail();
      String name = (String) payload.get("name");
      String avatar = (String) payload.get("picture");

      Account account = accountService.setGoogleAccount(email, name, avatar);
      AccountResponse response = AccountResponse.setResponse(account);
      String jwt = tokenProvider.generateToken(response);
      return ResponseEntity.ok(
          new ApiResponse<>(true, "Logged successfully", new LoginResponse(jwt)));
    } catch (BadRequestException | GeneralSecurityException | IOException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

}
