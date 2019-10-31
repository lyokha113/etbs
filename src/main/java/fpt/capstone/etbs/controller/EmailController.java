package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.DraftEmailCreateRequest;
import fpt.capstone.etbs.payload.SendEmailRequest;
import fpt.capstone.etbs.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.GeneralSecurityException;

@RestController
public class EmailController {

  @Autowired private EmailService emailSenderService;

  @PostMapping("/email/draft")
  public ResponseEntity<ApiResponse> makeDraftEmail(
      @Valid @RequestBody DraftEmailCreateRequest request) throws Exception {
    try {
      emailSenderService.makeDraftEmail(request);
    } catch (GeneralSecurityException e) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(false, "Invalid security information", null));
    }
    return ResponseEntity.ok(new ApiResponse<>(true, "Draft was made", null));
  }

  @PostMapping("/email/send/")
  public ResponseEntity<ApiResponse> sendEmail(@Valid @RequestBody SendEmailRequest request)
      throws Exception {
    emailSenderService.sendEmail(request);
    return ResponseEntity.ok(new ApiResponse<>(true, "Email was sent", null));
  }
}
