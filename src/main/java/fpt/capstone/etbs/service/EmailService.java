package fpt.capstone.etbs.service;

import fpt.capstone.etbs.payload.DraftEmailRequest;
import fpt.capstone.etbs.payload.SendEmailRequest;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

public interface EmailService {

  boolean sendEmail(UUID accountId, SendEmailRequest request) throws MessagingException, IOException;

  boolean makeDraftEmail(UUID accountId, DraftEmailRequest request)
      throws MessagingException, IOException, GeneralSecurityException;
}
