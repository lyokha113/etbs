package fpt.capstone.etbs.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.sendgrid.SendGrid;
import fpt.capstone.etbs.EtbsApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;

@Configuration
public class EmailConfig {

    @Value("${app.sendgrid.apiKey}")
    private String SENDGRID_API_KEY;

    private static final String CREDENTIALS_GOOGLE = "/etbs_gmail.json";
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(SENDGRID_API_KEY);
    }

    @Bean
    public GoogleClientSecrets googleClientSecrets() throws IOException {
        InputStream toLoad = EtbsApplication.class.getResourceAsStream(CREDENTIALS_GOOGLE);
        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(toLoad));
    }

}
