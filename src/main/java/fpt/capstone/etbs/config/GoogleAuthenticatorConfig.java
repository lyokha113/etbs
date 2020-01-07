package fpt.capstone.etbs.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleAuthenticatorConfig {

  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  @Value("${spring.security.oauth2.client.registration.google.clientId}")
  private String clientId;

  @Bean
  public GoogleIdTokenVerifier googleIdTokenVerifier()
      throws GeneralSecurityException, IOException {
    return new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(),
        JSON_FACTORY)
        .setAudience(Collections.singleton(clientId))
        .build();
  }
}
