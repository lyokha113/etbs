package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.SendEmailRequest;
import fpt.capstone.etbs.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class SendEmailController {

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/email/gmail")
    public ResponseEntity<ApiResponse> sendEmailByJava(
            @Valid @RequestBody SendEmailRequest request) throws Exception {
        emailSenderService.sendEmailByJava(
                request.getTo(),
                request.getSubject(),
                request.getContent());
        return ResponseEntity.ok(new ApiResponse<>(true, "Email sent", null));
    }

    @PostMapping("/email/sendgrid")
    public ResponseEntity<ApiResponse> sendEmailBySendGrid(
            @Valid @RequestBody SendEmailRequest request) throws Exception {
        emailSenderService.sendEmailBySendGrid(
                request.getTo(),
                request.getSubject(),
                request.getContent());
        return ResponseEntity.ok(new ApiResponse<>(true, "Email sent", null));
    }
}
