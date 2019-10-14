package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.payload.DraftEmailCreateRequest;
import fpt.capstone.etbs.service.EmailDraftService;
import fpt.capstone.etbs.util.DraftGmail;
import fpt.capstone.etbs.util.DraftOutlookMail;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public class EmailDraftServiceImpl implements EmailDraftService {

    @Override
    public void draftGmail(DraftEmailCreateRequest request) throws IOException, MessagingException, GeneralSecurityException {
      new DraftGmail(request);
    }

    @Override
    public void draftOutlook(DraftEmailCreateRequest request) throws MessagingException {
        new DraftOutlookMail().createDraftOutlook(request);
    }
}
