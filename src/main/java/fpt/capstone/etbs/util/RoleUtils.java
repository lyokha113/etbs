package fpt.capstone.etbs.util;

import fpt.capstone.etbs.constant.RoleEnum;
import org.springframework.security.core.Authentication;

public class RoleUtils {

  public static boolean hasAdminRole(Authentication auth) {
    if (auth == null) return false;
    return auth.getAuthorities().stream()
        .anyMatch(r -> r.getAuthority().contains(RoleEnum.ADMINISTRATOR.getName()));
  }
}
