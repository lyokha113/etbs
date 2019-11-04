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
import fpt.capstone.etbs.component.SendGridMail;
import fpt.capstone.etbs.constant.MailProvider;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.DraftEmailRequest;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

  private static final List<String> SCOPES =
      Arrays.asList(GmailScopes.GMAIL_INSERT, GmailScopes.MAIL_GOOGLE_COM);
  private static final String APPLICATION_NAME = "ETBS";
  private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private SendGrid sendGrid;

  @Autowired
  private GoogleClientSecrets googleClientSecrets;

  @Autowired
  private RawTemplateService rawTemplateService;

  @Override
  public void sendEmail(UUID accountId, SendEmailRequest request)
      throws MessagingException, IOException {
    String provider = request.getProvider();
    RawTemplate template = rawTemplateService.getRawTemplate(request.getRawTemplateId(), accountId);
    if (template == null) {
      throw new BadRequestException("Template doesn't existed");
    }
    String subject = template.getName().toUpperCase();
    String content = template.getCurrentVersion().getContent();

    if (provider.equalsIgnoreCase(MailProvider.GMAIL.name())) {
      javaMailSender.send(createMessage(request, subject, content));
    } else if (provider.equalsIgnoreCase(MailProvider.SENDGRID.name())) {
      sendEmailBySendGrid(request, subject, content);
    }
  }

  @Override
  public void makeDraftEmail(UUID accountId, DraftEmailRequest request)
      throws MessagingException, IOException, GeneralSecurityException {
    String provider = request.getProvider();
    RawTemplate template = rawTemplateService.getRawTemplate(request.getRawTemplateId(), accountId);
    if (template == null) {
      throw new BadRequestException("Template doesn't existed");
    }
    String subject = template.getName().toUpperCase();
    String content = template.getCurrentVersion().getContent();

    if (provider.equalsIgnoreCase(MailProvider.GMAIL.name())) {
      createDraftGMail(
          createMessage(request, subject, content, Session.getInstance(System.getProperties())),
          request.getEmail());
    } else if (provider.equalsIgnoreCase(MailProvider.YAHOO.name())) {
      createDraft(request, subject, content);
    } else if (provider.equalsIgnoreCase(MailProvider.OUTLOOK.name())) {
      createDraft(request, subject, content);
    }
  }

  private MimeMessage createMessage(SendEmailRequest request, String subject, String content)
      throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setTo(request.getTo());
    helper.setSubject(subject);
    helper.setText(content, true);
    return message;
  }

  private MimeMessage createMessage(DraftEmailRequest request, String subject, String content,
      Session session)
      throws MessagingException {
    MimeMessage message = new MimeMessage(session);
    message.setFlag(Flags.Flag.DRAFT, true);
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setSubject(subject);
    helper.setText(content, true);
    return message;
  }

  private void createDraft(DraftEmailRequest request, String subject, String content)
      throws MessagingException {
    Properties props = MailProvider.getMailConfig(request.getProvider());
    Session session = Session.getInstance(props);
    Store mailStore = session.getStore("imap");
    mailStore.connect(props.getProperty("host"), request.getEmail(), request.getPassword());
    Folder draftsMailBoxFolder = mailStore.getFolder(props.getProperty("draft"));
    draftsMailBoxFolder.open(Folder.READ_WRITE);
    javax.mail.Message[] draftMessages = {createMessage(request, subject, content, session)};
    draftsMailBoxFolder.appendMessages(draftMessages);
  }

  private void createDraftGMail(MimeMessage mm, String email)
      throws MessagingException, IOException, GeneralSecurityException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    mm.writeTo(baos);
    String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
    Message message = new Message();
    message.setRaw(encodedEmail);
    Draft draft = new Draft();
    draft.setMessage(message);
    this.getGMailInstance().users().drafts().create(email, draft).execute();
  }

  private void sendEmailBySendGrid(SendEmailRequest request, String subject, String content)
      throws IOException {
    Email sender = new Email("etbsonline@gmail.com");
    Email receiver = new Email(request.getTo());
    Content body = new Content("text/html", content);
    SendGridMail mail = new SendGridMail(sender, subject, receiver, body);

    Request re = new Request();
    re.setMethod(Method.POST);
    re.setEndpoint("mail/send");
    re.setBody(mail.build());
    sendGrid.api(re);
  }

  private Gmail getGMailInstance() throws GeneralSecurityException, IOException {
    NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(
            netHttpTransport, JSON_FACTORY, googleClientSecrets, SCOPES)
            .setAccessType("offline")
            .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

    return new Gmail.Builder(netHttpTransport, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }
}
