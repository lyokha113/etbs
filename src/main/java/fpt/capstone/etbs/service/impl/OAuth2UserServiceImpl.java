package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.exception.OAuth2AuthenticationProcessingException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.constant.AuthProvider;
import fpt.capstone.etbs.model.Role;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.component.GoogleOAuth2UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    @Autowired
    private AccountRepository accountRepository;

    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        GoogleOAuth2UserInfo oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        Optional<Account> userOptional = accountRepository.getByEmail(oAuth2UserInfo.getEmail());
        Account account;
        if (userOptional.isPresent()) {
            account = userOptional.get();
            if (!account.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        account.getProvider() + " account. Please use your " + account.getProvider() +
                        " account to login.");
            }
            account = updateExistingUser(account, oAuth2UserInfo);
        } else {
            account = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        return UserPrincipal.create(account, oAuth2User.getAttributes());
    }

    private Account registerNewUser(OAuth2UserRequest oAuth2UserRequest, GoogleOAuth2UserInfo oAuth2UserInfo) {
        Account account = new Account();
        account.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
//        account.setProviderId(oAuth2UserInfo.getId());
        account.setFullName(oAuth2UserInfo.getName());
        account.setEmail(oAuth2UserInfo.getEmail());
        account.setImageUrl(oAuth2UserInfo.getImageUrl());
        account.setRole(Role.builder().id(RoleEnum.USER.getId()).build());
        account.setActive(true);
        return accountRepository.save(account);
    }

    private Account updateExistingUser(Account existingUser, GoogleOAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFullName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return accountRepository.save(existingUser);
    }
}
