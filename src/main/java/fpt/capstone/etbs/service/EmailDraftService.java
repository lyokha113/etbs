package fpt.capstone.etbs.service;

import fpt.capstone.etbs.payload.DraftEmailCreateRequest;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface EmailDraftService {
    void draftGmail(DraftEmailCreateRequest request) throws IOException, MessagingException, GeneralSecurityException;
    void draftOutlook(DraftEmailCreateRequest request) throws MessagingException;
}
