package fpt.capstone.etbs.service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import fpt.capstone.etbs.service.EmailSenderService;
import fpt.capstone.etbs.util.SendGridMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${app.sendgrid.apiKey}")
    private String SENDGRID_API_KEY;

    @Override
    public void sendEmailByJava(String to, String subject, String msg) throws MessagingException {
        MimeMessage mime = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(msg, true);
        javaMailSender.send(mime);
    }

    @Override
    public void sendEmailBySendGrid(String to, String subject, String content) throws IOException {
        Email sender = new Email("etbsonline@gmail.com");
        Email receiver = new Email(to);
        Content body = new Content("text/html", content);
        SendGridMail mail = new SendGridMail(sender, subject, receiver, body);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        sg.api(request);

    }
}
