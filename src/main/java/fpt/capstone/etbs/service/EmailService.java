package fpt.capstone.etbs.service;

import fpt.capstone.etbs.payload.DraftEmailRequest;
import fpt.capstone.etbs.payload.SendEmailRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.mail.MessagingException;

public interface EmailService {

  void sendEmail(UUID accountId, SendEmailRequest request)
      throws MessagingException, IOException;

  void makeDraftEmail(UUID accountId, DraftEmailRequest request)
      throws MessagingException, IOException, GeneralSecurityException;
}
