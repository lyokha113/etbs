package fpt.capstone.etbs.service;

import fpt.capstone.etbs.payload.DraftEmailRequest;
import fpt.capstone.etbs.payload.SendEmailRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;
import javax.mail.MessagingException;

public interface EmailService {

  void sendEmail(UUID accountId, SendEmailRequest request)
      throws MessagingException, IOException;

  void makeDraftYahoo(UUID accountId, DraftEmailRequest request) throws MessagingException;

  void makeDraftOutlook(UUID accountId, DraftEmailRequest request) throws MessagingException;

  void makeDraftGMail(String state, String code)
      throws MessagingException, IOException, GeneralSecurityException;

  void sendConfirmUserEmail(String email, String token) throws MessagingException, IOException;

  void sendConfirmAccount(String email, String token) throws MessagingException, IOException;

  void sendConfirmRecovery(String email, String token) throws MessagingException, IOException;

  void sendRecovery(String email, String password) throws MessagingException, IOException;
}
