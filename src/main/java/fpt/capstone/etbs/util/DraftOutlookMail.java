package fpt.capstone.etbs.util;

import fpt.capstone.etbs.payload.DraftEmailCreateRequest;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class DraftOutlookMail {
    public MimeMessage createMessage(String subject, String body) throws MessagingException {
        MimeMessage message = new MimeMessage(Session.getInstance(System.getProperties()));
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setText(body, true);
        helper.setSubject(subject);
        return message;
    }

    public void createDraftOutlook(DraftEmailCreateRequest request) throws MessagingException {
        Properties props = new Properties();
        props.setProperty("mail.imap.ssl.enable", "true");
        Session mailSession = Session.getInstance(props);
        mailSession.setDebug(true);
        Store mailStore = mailSession.getStore("imap");
        mailStore.connect("outlook.office365.com", request.getGmail(), request.getPassword());
        Message draftMessage = createMessage(request.getSubject(), request.getBody());
        Folder draftsMailBoxFolder = mailStore.getFolder("Drafts");
        draftsMailBoxFolder.open(Folder.READ_WRITE);
        draftMessage.setFlag(Flags.Flag.DRAFT, true);
        Message[] draftMessages = {draftMessage};
        draftsMailBoxFolder.appendMessages(draftMessages);
    }
}
