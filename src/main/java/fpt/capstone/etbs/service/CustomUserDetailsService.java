package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

public interface CustomUserDetailsService  {
    UserDetails loadUserFromAccount(Account account);
}
