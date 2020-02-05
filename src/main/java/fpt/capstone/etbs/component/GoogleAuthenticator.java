package fpt.capstone.etbs.component;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthenticator {

  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  @Value("${spring.security.oauth2.client.registration.google.clientId}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.google.clientSecret}")
  private String clientSecret;


  public GoogleAuthorizationCodeFlow getFlow() throws GeneralSecurityException, IOException {
    Details web = new Details();
    web.setClientId(clientId);
    web.setClientSecret(clientSecret);
    GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    return new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
        Arrays.asList(GmailScopes.MAIL_GOOGLE_COM, GmailScopes.GMAIL_INSERT))
        .setAccessType("online")
        .setApprovalPrompt("force")
        .build();
  }

  public Gmail getGMailInstance(String redirectUri, String code) throws GeneralSecurityException, IOException {
    GoogleAuthorizationCodeFlow flow = getFlow();
    TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
    Credential credential = flow.createAndStoreCredential(response, null);
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
        .setApplicationName("ETBS").build();
  }

  public String authorize(String redirectUri, String state) throws GeneralSecurityException, IOException {
    AuthorizationCodeRequestUrl authorizationUrl = getFlow()
        .newAuthorizationUrl()
        .setState(state)
        .setRedirectUri(redirectUri);
    return authorizationUrl.build();
  }
}
