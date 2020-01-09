package fpt.capstone.etbs.service.impl;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;
import com.sendgrid.SendGrid;
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

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private SendGrid sendGrid;

  @Autowired
  private RawTemplateService rawTemplateService;

//    @Override
//    @Async("mailAsyncExecutor")
//    public void sendEmail(UUID accountId, SendEmailRequest request)
//            throws MessagingException, IOException {
//
//        RawTemplate template = rawTemplateService.getRawTemplate(request.getRawTemplateId(), accountId);
//        if (template == null) {
//            throw new BadRequestException("Template doesn't existed");
//        }
//
//        String provider = request.getProvider();
//        String subject = template.getName().toUpperCase();
//        String content = template.getCurrentVersion().getContent();
//
//        if (provider.equalsIgnoreCase(MailProvider.GMAIL.name())) {
//            javaMailSender.send(createMessage(request.getTo()[0], subject, content));
//        } else if (provider.equalsIgnoreCase(MailProvider.SENDGRID.name())) {
//            sendEmailBySendGrid(request, subject, content);
//        }
//    }


  @Override
  public void makeDraftYahoo(UUID accountId, DraftEmailRequest request)
      throws MessagingException {
    MimeMessage draftMessage = createDraftMessage(request.getRawTemplateId(), accountId);
    createDraft(request.getEmail(), request.getPassword(), MailProvider.YAHOO, draftMessage);
  }

  @Override
  public void makeDraftOutlook(UUID accountId, DraftEmailRequest request)
      throws MessagingException {
    MimeMessage draftMessage = createDraftMessage(request.getRawTemplateId(), accountId);
    createDraft(request.getEmail(), request.getPassword(), MailProvider.OUTLOOK, draftMessage);
  }

  @Override
  public void makeDraftGMail(UUID accountId, Integer rawId, Gmail gmail)
      throws MessagingException, IOException {
    MimeMessage draftMessage = createDraftMessage(rawId, accountId);
    createDraft(draftMessage, gmail);
  }


  private MimeMessage createDraftMessage(Integer rawId, UUID accountId)
      throws MessagingException {

    RawTemplate template = rawTemplateService.getRawTemplate(rawId, accountId);
    if (template == null) {
      throw new BadRequestException("Template doesn't existed");
    }

    String subject = template.getName().toUpperCase();
    String content = template.getCurrentVersion().getContent();

    MimeMessage message = javaMailSender.createMimeMessage();
    message.setFlag(Flags.Flag.DRAFT, true);
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setSubject("ETBS - " + subject);
    helper.setText(content, true);
    return message;
  }


  private void createDraft(String email, String password, MailProvider provider,
      MimeMessage draftMessage)
      throws MessagingException {
    Properties props = MailProvider.getMailConfig(provider);
    Session session = Session.getInstance(props);
    Store mailStore = session.getStore("imap");
    mailStore.connect(props.getProperty("host"), email, password);
    Folder folder = mailStore.getFolder(props.getProperty("draft"));
    folder.open(Folder.READ_WRITE);
    javax.mail.Message[] draft = {draftMessage};
    folder.appendMessages(draft);
  }

  private void createDraft(MimeMessage mimeMessage, Gmail gmail)
      throws MessagingException, IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    mimeMessage.writeTo(out);
    String encodedEmail = Base64.encodeBase64URLSafeString(out.toByteArray());
    Message message = new Message();
    message.setRaw(encodedEmail);
    Draft draft = new Draft();
    draft.setMessage(message);
    gmail.users().drafts().create("me", draft).execute();
  }


  @Override
  public void sendEmail(UUID accountId, SendEmailRequest request)
      throws MessagingException, IOException {

  }


  @Override
  public void sendConfirmUserEmail(SendConfirmEmailRequest request) throws MessagingException {

  }

  @Override
  public void sendConfirmAccount(SendConfirmEmailRequest request) throws MessagingException {

  }


//    @Override
//    public void sendConfirmUserEmail(SendConfirmEmailRequest request)
//            throws MessagingException {
//        String content = AppConstant.EMAIL_CONFIRM_CONTENT_1 + request.getToken()
//                        + AppConstant.EMAIL_CONFIRM_CONTENT_2;
//        javaMailSender.send(createMessage(request.getEmail(), AppConstant.EMAIL_CONFIRM_SUBJECT, content));
//    }
//
//    @Override
//    public void sendConfirmAccount(SendConfirmEmailRequest request) throws MessagingException {
//        String content = AppConstant.ACCOUNT_CONFIRM_CONTENT_1 + request.getToken()
//                        + AppConstant.ACCOUNT_CONFIRM_CONTENT_2;
//      javaMailSender.send(createMessage(request.getEmail(), AppConstant.ACCOUNT_CONFIRM_SUBJECT, content));
//    }

//    private MimeMessage createMessage(String receiver, String subject, String content)
//            throws MessagingException {
//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//        helper.setTo(receiver);
//        helper.setSubject(subject);
//        helper.setText(content, true);
//        return message;
//    }
//
//
//
//    private void sendEmailBySendGrid(SendEmailRequest request, String subject, String content)
//            throws IOException {
//
//        SendGridMail mail = new SendGridMail();
//        mail.setSubject(subject);
//        Email sender = new Email("etbsonline@gmail.com");
//        mail.setFrom(sender);
//
//        Personalization personalization = new Personalization();
//        String[] receivers = request.getTo();
//        for (String receiver : receivers) {
//            Email email = new Email(receiver);
//            personalization.addTo(email);
//        }
//        personalization.setSubject(subject);
//        mail.addPersonalization(personalization);
//
//        Content body = new Content("text/html", content);
//        mail.addContent(body);
//
//        Request re = new Request();
//        re.setMethod(Method.POST);
//        re.setEndpoint("mail/send");
//        re.setBody(mail.build());
//        sendGrid.api(re);
//    }


}
