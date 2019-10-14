package fpt.capstone.etbs.util;

import fpt.capstone.etbs.payload.DraftEmailCreateRequest;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class DraftEmail {
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
        MailConfigToServer(request, props, "outlook.office365.com", "Drafts");
    }

    public void createDraftYahoo(DraftEmailCreateRequest request) throws MessagingException {
        Properties props = new Properties();
        props.setProperty("mail.imap.ssl.enable", "true");
        props.put("mail.smtp.host", "smtp.mail.yahoo.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtp.port", 465);
        MailConfigToServer(request, props, "imap.mail.yahoo.com", "Draft");
    }

    private void MailConfigToServer(DraftEmailCreateRequest request, Properties props, String host, String folder) throws MessagingException {
        Session mailSession = Session.getInstance(props);
        mailSession.setDebug(true);
        Store mailStore = mailSession.getStore("imap");
        mailStore.connect(host, request.getEmail(), request.getPassword());
        Message draftMessage = createMessage(request.getSubject(), request.getBody());
        Folder draftsMailBoxFolder = mailStore.getFolder(folder);
        draftsMailBoxFolder.open(Folder.READ_WRITE);
        draftMessage.setFlag(Flags.Flag.DRAFT, true);
        Message[] draftMessages = {draftMessage};
        draftsMailBoxFolder.appendMessages(draftMessages);
    }
}
