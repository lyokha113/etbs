package fpt.capstone.etbs.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.sendgrid.SendGrid;
import fpt.capstone.etbs.EtbsApplication;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

  @Value("${app.sendgrid.apiKey}")
  private String SENDGRID_API_KEY;

  @Bean
  public SendGrid sendGrid() {
    return new SendGrid(SENDGRID_API_KEY);
  }


}
