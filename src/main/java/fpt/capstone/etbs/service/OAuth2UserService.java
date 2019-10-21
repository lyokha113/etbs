package fpt.capstone.etbs.service;


import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserService {
    OAuth2User loadAccount(OAuth2UserRequest oAuth2UserRequest);
}
