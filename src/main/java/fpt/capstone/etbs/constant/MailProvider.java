package fpt.capstone.etbs.constant;

import fpt.capstone.etbs.exception.BadRequestException;
import java.util.Properties;

public enum MailProvider {
  GMAIL,
  YAHOO,
  OUTLOOK,
  SENDGRID;

  public static Properties getMailConfig(MailProvider provider) {
    Properties props = new Properties();
    props.setProperty("mail.imap.ssl.enable", "true");

    if (provider.equals(OUTLOOK)) {
      props.setProperty("host", "outlook.office365.com");
      props.setProperty("draft", "Drafts");
    } else if (provider.equals(YAHOO)) {
      props.setProperty("mail.imap.ssl.enable", "true");
      props.setProperty("mail.smtp.host", "smtp.mail.yahoo.com");
      props.setProperty("mail.smtp.auth", "true");
      props.setProperty("mail.debug", "false");
      props.put("mail.smtp.port", 456);
      props.setProperty("host", "imap.mail.yahoo.com");
      props.setProperty("draft", "Draft");
    } else {
      throw new BadRequestException("Mail provider isn't existed");
    }

    return props;
  }
}
