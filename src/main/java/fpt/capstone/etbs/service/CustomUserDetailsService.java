package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Account;
import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetailsService {

  UserDetails loadUserFromID(UUID id);
}
