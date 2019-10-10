package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.SendEmailRequest;
import fpt.capstone.etbs.service.EmailDraftService;
import fpt.capstone.etbs.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class EmailController {

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    EmailDraftService emailDraftService;

    @PostMapping("/email/draft/gmail")
    public ResponseEntity<ApiResponse> draftGmail() throws Exception {
        emailDraftService.draftGmail();
        return ResponseEntity.ok(new ApiResponse<>(true, "Email Draft", null));
    }
    @PostMapping("/email/draft/outlook")
    public ResponseEntity<ApiResponse> draftOutlook() throws Exception {
        emailDraftService.draftOutlook();
        return ResponseEntity.ok(new ApiResponse<>(true, "Email Draft", null));
    }
    @PostMapping("/email/send/gmail")
    public ResponseEntity<ApiResponse> sendEmailByJava(
            @Valid @RequestBody SendEmailRequest request) throws Exception {
        emailSenderService.sendEmailByJava(
                request.getTo(),
                request.getSubject(),
                request.getContent());
        return ResponseEntity.ok(new ApiResponse<>(true, "Email sent", null));
    }

    @PostMapping("/email/send/sendgrid")
    public ResponseEntity<ApiResponse> sendEmailBySendGrid(
            @Valid @RequestBody SendEmailRequest request) throws Exception {
        emailSenderService.sendEmailBySendGrid(
                request.getTo(),
                request.getSubject(),
                request.getContent());
        return ResponseEntity.ok(new ApiResponse<>(true, "Email sent", null));
    }
}
