package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.DraftEmailCreateRequest;
import fpt.capstone.etbs.payload.SendEmailRequest;
import fpt.capstone.etbs.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailSenderService;

    @PostMapping("/email/draft")
    public ResponseEntity<ApiResponse> makeDraftEmail(@Valid @RequestBody DraftEmailCreateRequest request) throws Exception {
        emailSenderService.makeDraftEmail(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Draft was made", null));
    }

    @PostMapping("/email/send/")
    public ResponseEntity<ApiResponse> sendEmail(@Valid @RequestBody SendEmailRequest request) throws Exception {
        emailSenderService.sendEmail(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Email was sent", null));
    }


}
