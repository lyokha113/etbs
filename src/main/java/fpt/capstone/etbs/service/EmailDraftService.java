package fpt.capstone.etbs.service;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailDraftService {
    void draftGmail() throws IOException, MessagingException;
    void draftOutlook() throws IOException, MessagingException;
}
