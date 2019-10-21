package fpt.capstone.etbs.service;

import fpt.capstone.etbs.constant.MailProvider;
import fpt.capstone.etbs.payload.DraftEmailCreateRequest;
import fpt.capstone.etbs.payload.SendEmailRequest;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface EmailService {

    boolean sendEmail(SendEmailRequest request) throws MessagingException, IOException;
    boolean makeDraftEmail(DraftEmailCreateRequest request) throws MessagingException, IOException, GeneralSecurityException;
}
