package fpt.capstone.etbs.component;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthenticator {

  private static final JacksonFactory jacksonFactory = new JacksonFactory();

  @Value("${spring.security.oauth2.client.registration.google.clientId}")
  private String clientId;

  @Bean
  public GoogleIdTokenVerifier googleIdTokenVerifier() {
    return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), jacksonFactory)
        .setAudience(Collections.singleton(clientId))
        .build();
  }
}
