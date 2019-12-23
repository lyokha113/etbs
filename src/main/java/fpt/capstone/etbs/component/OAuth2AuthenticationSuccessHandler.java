package fpt.capstone.etbs.component;

import static fpt.capstone.etbs.component.HttpCookieOAuth2AuthorizationRequest.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.payload.AccountResponse;
import fpt.capstone.etbs.util.CookieUtils;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Value("${app.oauth2.authorizedRedirectUris}")
  private String authorizedRedirectUri;

  @Autowired
  private JwtTokenProvider tokenProvider;

  @Autowired
  private HttpCookieOAuth2AuthorizationRequest httpCookieOAuth2AuthorizationRequest;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    String targetUrl = determineTargetUrl(request, response, authentication);

    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  private String determineTargetUrl(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws JsonProcessingException {
    Optional<String> redirectUri =
        CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

    if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
      throw new BadRequestException(
          "Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
    }

    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

    UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
    AccountResponse account = AccountResponse.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .imageUrl(user.getImageUrl())
        .active(true)
        .roleName(RoleEnum.USER.getName())
        .roleId(RoleEnum.USER.getId())
        .build();
    String token = tokenProvider.generateToken(account);

    return UriComponentsBuilder.fromUriString(targetUrl)
        .queryParam("token", token)
        .build()
        .toUriString();
  }

  private void clearAuthenticationAttributes(
      HttpServletRequest request, HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    httpCookieOAuth2AuthorizationRequest.removeAuthorizationRequestCookies(request, response);
  }

  private boolean isAuthorizedRedirectUri(String uri) {
    URI clientRedirectUri = URI.create(uri);
    URI authorizedURI = URI.create(authorizedRedirectUri);
    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
        && authorizedURI.getPort() == clientRedirectUri.getPort();
  }
}
