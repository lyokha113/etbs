package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Account;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetailsService {

  UserDetails loadUserFromAccount(Account account);
}
