package fpt.capstone.etbs.component;

import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.security.OAuth2AuthenticationFailureHandler;
import fpt.capstone.etbs.security.OAuth2AuthenticationSuccessHandler;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.CustomUserDetailsService;
import fpt.capstone.etbs.service.OAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsSecurity implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountService.getAccountByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("User not found with  email : " + email);
        }
        return UserPrincipal.create(account);
    }

    @Transactional
    public UserDetails loadUserFromAccount(Account account) throws UsernameNotFoundException {
        if (account == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return UserPrincipal.create(account);
    }
}