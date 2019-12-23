package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService, UserDetailsService {

  @Autowired
  private AccountRepository accountRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Account account = accountRepository.getByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
    return UserPrincipal.create(account);
  }

  @Override
  public UserDetails loadUserFromAccount(Account account) {
    if (account == null) {
      throw new UsernameNotFoundException("User not found");
    }
    account = accountRepository.findById(account.getId())
        .orElseThrow(() -> new UsernameNotFoundException("User not found\""));
    return UserPrincipal.create(account);
  }
}
