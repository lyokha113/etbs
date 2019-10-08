package fpt.capstone.etbs.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

public interface CustomUserDetailsService {
    UserDetails loadUserByUsername(String var1) throws UsernameNotFoundException;
    UserDetails loadUserById(UUID id);
}
