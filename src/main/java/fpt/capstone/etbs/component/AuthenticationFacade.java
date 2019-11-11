package fpt.capstone.etbs.component;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacade {

  Authentication getAuthentication();
}
