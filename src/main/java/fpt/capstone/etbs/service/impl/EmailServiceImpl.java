package fpt.capstone.etbs.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import fpt.capstone.etbs.component.SendGridMail;
import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.MailProvider;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.DraftEmailRequest;
import fpt.capstone.etbs.payload.SendConfirmEmailRequest;
import fpt.capstone.etbs.payload.SendEmailRequest;
import fpt.capstone.etbs.service.EmailService;
import fpt.capstone.etbs.service.RawTemplateService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SendGrid sendGrid;

    @Autowired
    private RawTemplateService rawTemplateService;

    @Override
    @Async("mailAsyncExecutor")
    public void sendEmail(UUID accountId, SendEmailRequest request)
            throws MessagingException, IOException {

        RawTemplate template = rawTemplateService.getRawTemplate(request.getRawTemplateId(), accountId);
        if (template == null) {
            throw new BadRequestException("Template doesn't existed");
        }

        String provider = request.getProvider();
        String subject = template.getName().toUpperCase();
        String content = template.getCurrentVersion().getContent();

        if (provider.equalsIgnoreCase(MailProvider.GMAIL.name())) {
            javaMailSender.send(createMessage(request.getTo()[0], subject, content));
        } else if (provider.equalsIgnoreCase(MailProvider.SENDGRID.name())) {
            sendEmailBySendGrid(request, subject, content);
        }
    }

    @Override
    public void makeDraftEmail(UUID accountId, DraftEmailRequest request)
            throws MessagingException, IOException, GeneralSecurityException {

        RawTemplate template = rawTemplateService.getRawTemplate(request.getRawTemplateId(), accountId);
        if (template == null) {
            throw new BadRequestException("Template doesn't existed");
        }

        String provider = request.getProvider();
        String subject = template.getName().toUpperCase();
        String content = template.getCurrentVersion().getContent();

        if (provider.equalsIgnoreCase(MailProvider.GMAIL.name())) {
            Session session = Session.getInstance(System.getProperties());
            MimeMessage mimeMessage = createMessage(subject, content, session);
            createDraftGMail(null, mimeMessage, request.getEmail());
        } else if (provider.equalsIgnoreCase(MailProvider.YAHOO.name())) {
            createDraft(request, subject, content);
        } else if (provider.equalsIgnoreCase(MailProvider.OUTLOOK.name())) {
            createDraft(request, subject, content);
        }
    }

    @Override
    public void sendConfirmUserEmail(SendConfirmEmailRequest request)
            throws MessagingException {
        String content = AppConstant.EMAIL_CONFIRM_CONTENT_1 + request.getToken()
                        + AppConstant.EMAIL_CONFIRM_CONTENT_2;
        javaMailSender.send(createMessage(request.getEmail(), AppConstant.EMAIL_CONFIRM_SUBJECT, content));
    }

    @Override
    public void sendConfirmAccount(SendConfirmEmailRequest request) throws MessagingException {
        String content = AppConstant.ACCOUNT_CONFIRM_CONTENT_1 + request.getToken()
                        + AppConstant.ACCOUNT_CONFIRM_CONTENT_2;
      javaMailSender.send(createMessage(request.getEmail(), AppConstant.ACCOUNT_CONFIRM_SUBJECT, content));
    }

    private MimeMessage createMessage(String receiver, String subject, String content)
            throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(receiver);
        helper.setSubject(subject);
        helper.setText(content, true);
        return message;
    }

    private MimeMessage createMessage(String subject, String content, Session session)
            throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFlag(Flags.Flag.DRAFT, true);
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setSubject(subject);
        helper.setText(content, true);
        return message;
    }

    private void sendEmailBySendGrid(SendEmailRequest request, String subject, String content)
            throws IOException {

        SendGridMail mail = new SendGridMail();
        mail.setSubject(subject);
        Email sender = new Email("etbsonline@gmail.com");
        mail.setFrom(sender);

        Personalization personalization = new Personalization();
        String[] receivers = request.getTo();
        for (String receiver : receivers) {
            Email email = new Email(receiver);
            personalization.addTo(email);
        }
        personalization.setSubject(subject);
        mail.addPersonalization(personalization);

        Content body = new Content("text/html", content);
        mail.addContent(body);

        Request re = new Request();
        re.setMethod(Method.POST);
        re.setEndpoint("mail/send");
        re.setBody(mail.build());
        sendGrid.api(re);
    }

    private void createDraft(DraftEmailRequest request, String subject, String content)
            throws MessagingException {
        Properties props = MailProvider.getMailConfig(request.getProvider());
        Session session = Session.getInstance(props);
        Store mailStore = session.getStore("imap");
        mailStore.connect(props.getProperty("host"), request.getEmail(), request.getPassword());
        Folder folder = mailStore.getFolder(props.getProperty("draft"));
        folder.open(Folder.READ_WRITE);
        javax.mail.Message[] draft = {createMessage("ETBS-" + subject, content, session)};
        folder.appendMessages(draft);
    }

    private void createDraftGMail(Gmail gmail, MimeMessage mimeMessage, String email)
            throws MessagingException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mimeMessage.writeTo(out);
        String encodedEmail = Base64.encodeBase64URLSafeString(out.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        Draft draft = new Draft();
        draft.setMessage(message);
        gmail.users().drafts().create(email, draft).execute();
    }
}
