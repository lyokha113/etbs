package fpt.capstone.etbs.constant;

import java.util.Properties;

public enum MailProvider {
  GMAIL,
  YAHOO,
  OUTLOOK,
  SENDGRID;

  public static Properties getMailConfig(String provider) {
    Properties props = new Properties();
    props.setProperty("mail.imap.ssl.enable", "true");

    if (provider.equals(OUTLOOK.name())) {
      props.setProperty("host", "outlook.office365.com");
      props.setProperty("draft", "Drafts");
      return props;
    }

    if (provider.equals(YAHOO.name())) {
      props.setProperty("mail.imap.ssl.enable", "true");
      props.setProperty("mail.smtp.host", "smtp.mail.yahoo.com");
      props.setProperty("mail.smtp.auth", "true");
      props.setProperty("mail.debug", "false");
      props.put("mail.smtp.port", 456);
      props.setProperty("host", "imap.mail.yahoo.com");
      props.setProperty("draft", "Draft");
      return props;
    }

    return null;
  }
}
