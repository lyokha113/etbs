package fpt.capstone.etbs.service;

import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetailsService {

  UserDetails loadUserFromID(UUID id);
}
