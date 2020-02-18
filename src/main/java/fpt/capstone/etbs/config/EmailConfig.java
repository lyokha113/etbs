package fpt.capstone.etbs.config;

import com.sendgrid.SendGrid;
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

  @Bean
  public int sendMailType() {
    // send by gmail if <= 0 - send by sendgrid if > 0
    return 0;
  }

}
