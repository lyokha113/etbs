package fpt.capstone.etbs.service;

import fpt.capstone.etbs.payload.DraftEmailCreateRequest;
import fpt.capstone.etbs.payload.SendEmailRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.mail.MessagingException;

public interface EmailService {

  boolean sendEmail(SendEmailRequest request) throws MessagingException, IOException;

  boolean makeDraftEmail(DraftEmailCreateRequest request)
      throws MessagingException, IOException, GeneralSecurityException;
}
