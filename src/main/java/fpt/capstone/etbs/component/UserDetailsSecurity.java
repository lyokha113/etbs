package fpt.capstone.etbs.component;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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