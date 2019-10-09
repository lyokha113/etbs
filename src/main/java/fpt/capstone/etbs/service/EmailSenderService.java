package fpt.capstone.etbs.service;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailSenderService {
    void sendEmailByJava(String to, String subject, String content) throws MessagingException;
    void sendEmailBySendGrid(String to, String subject, String content) throws IOException;
}
