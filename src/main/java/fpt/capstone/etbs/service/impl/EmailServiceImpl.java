package fpt.capstone.etbs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import fpt.capstone.etbs.component.GoogleAuthenticator;
import fpt.capstone.etbs.component.SendGridMail;
import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.MailProvider;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.DraftEmailRequest;
import fpt.capstone.etbs.payload.DynamicData;
import fpt.capstone.etbs.payload.DynamicDataAttrs;
import fpt.capstone.etbs.payload.SendEmailRequest;
import fpt.capstone.etbs.service.EmailService;
import fpt.capstone.etbs.service.RawTemplateService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private SendGrid sendGrid;

  @Autowired
  private RawTemplateService rawTemplateService;

  @Autowired
  private GoogleAuthenticator googleAuthenticator;

  @Autowired
  private int sendMailType;

  @Value("${app.serverConfirmEmailUri}")
  private String serverConfirmEmailUri;

  @Override
  @Async("mailAsyncExecutor")
  public void sendEmail(UUID accountId, SendEmailRequest request)
      throws MessagingException, IOException {

    RawTemplate template = rawTemplateService.getRawTemplate(request.getRawId(), accountId);
    if (template == null) {
      throw new BadRequestException("Template doesn't existed");
    }

    String subject = template.getName().toUpperCase();
    String content = template.getContent();

    List<DynamicData> dataSet = request.getData();
    if (dataSet.stream().anyMatch(data -> !data.getAttrs().isEmpty())) {
      for (DynamicData data : dataSet) {
        String email = data.getEmail();
        String modifiedContent = setDynamicData(content, data.getAttrs());

        if (sendMailType <= 0) {
          javaMailSender.send(createSendMessage(email, subject, modifiedContent));
          sendMailType++;
        } else {
          sendBySendGrid(email, subject, modifiedContent);
          sendMailType--;
        }
      }
    } else {
      List<String> receivers = dataSet.stream().map(DynamicData::getEmail)
          .collect(Collectors.toList());
      String[] emails = new String[receivers.size()];
      receivers.toArray(emails);

      if (sendMailType <= 0) {
        javaMailSender.send(createSendMessage(emails, subject, content));
        sendMailType++;
      } else {
        sendBySendGrid(emails, subject, content);
        sendMailType--;
      }
    }
  }


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
  public void makeDraftGMail(String state, String code)
      throws MessagingException, IOException, GeneralSecurityException {

    UUID accountId;
    int rawId;
    String redirectUri;
    try {
      byte[] decodedState = java.util.Base64.getUrlDecoder().decode(state);
      Map mapState = JSON_MAPPER.readValue(decodedState, Map.class);
      accountId = UUID.fromString(String.valueOf(mapState.get("accountId")));
      rawId = Integer.parseInt(String.valueOf(mapState.get("rawId")));
      redirectUri = String.valueOf(mapState.get("redirectUri"));
    } catch (Exception e) {
      throw new BadRequestException("Invalid state information");
    }

    Gmail gmail = googleAuthenticator.getGMailInstance(redirectUri, code);
    MimeMessage draftMessage = createDraftMessage(rawId, accountId);
    createDraft(draftMessage, gmail);
  }


  @Override
  public void sendConfirmUserEmail(String email, String token) throws MessagingException {
    String content = setTokenUserEmail(token);
    javaMailSender.send(createSendMessage(email, AppConstant.EMAIL_CONFIRM_SUBJECT, content));
  }

  @Override
  public void sendConfirmAccount(String email, String token) throws MessagingException {
    String content = AppConstant.ACCOUNT_CONFIRM_CONTENT_1 + token
        + AppConstant.ACCOUNT_CONFIRM_CONTENT_2;
    javaMailSender.send(createSendMessage(email, AppConstant.ACCOUNT_CONFIRM_SUBJECT, content));
  }

  private MimeMessage createDraftMessage(Integer rawId, UUID accountId)
      throws MessagingException {

    RawTemplate template = rawTemplateService.getRawTemplate(rawId, accountId);
    if (template == null) {
      throw new BadRequestException("Template doesn't existed");
    }

    String subject = template.getName().toUpperCase();
    String content = template.getContent();

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

  private MimeMessage createSendMessage(String receiver, String subject, String content)
      throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setTo(receiver);
    helper.setSubject(subject);
    helper.setText(content, true);
    return message;
  }

  private MimeMessage createSendMessage(String[] receiver, String subject, String content)
      throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setTo(receiver);
    helper.setSubject(subject);
    helper.setText(content, true);
    return message;
  }

  private String setDynamicData(String content, List<DynamicDataAttrs> attrs) {

    if (attrs.isEmpty()) {
      return content;
    }

    Document doc = Jsoup.parse(content, "UTF-8");
    attrs.forEach(attr -> {
      String cssQuery =
          "[datatype=" + attr.getDatatype() + "]"
              + "[name=" + attr.getName() + "]";
      Element ele = doc.select(cssQuery).first();
      if (ele != null) {
        String value = attr.getValue();
        if (attr.getDatatype().equalsIgnoreCase("dynamic text")) {
          ele.text(value);
        } else if (attr.getDatatype().equalsIgnoreCase("dynamic link")) {
          ele.attr("href", value);
        }
      }
    });
    return doc.outerHtml();
  }

  private String setTokenUserEmail(String token) {
    Document doc = Jsoup.parse(AppConstant.EMAIL_CONFIRM_CONTENT, "UTF-8");
    String cssQuery = "#token";
    Element ele = doc.select(cssQuery).first();
    ele.attr("href", serverConfirmEmailUri + "?token=" + token);
    return doc.outerHtml();
  }


  private void sendBySendGrid(String receiver, String subject, String content)
      throws IOException {

    Email from = new Email("etbsonline@gmail.com");
    Email to = new Email(receiver);
    Content body = new Content("text/html", content);
    Mail mail = new Mail(from, subject, to, body);

    Request request = new Request();
    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");
    request.setBody(mail.build());
    sendGrid.api(request);
  }

  private void sendBySendGrid(String[] receivers, String subject, String content)
      throws IOException {

    SendGridMail mail = new SendGridMail();
    mail.setSubject(subject);
    Email sender = new Email("etbsonline@gmail.com");
    mail.setFrom(sender);

    Personalization personalization = new Personalization();
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

}
