package fpt.capstone.etbs.component;

import fpt.capstone.etbs.model.Account;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class UserPrincipal implements UserDetails {

  private UUID id;
  private String fullName;
  private String email;
  private boolean active;
  private String imageUrl;
  private String password;
  private Collection<? extends GrantedAuthority> authorities;
  private Map<String, Object> attributes;

  public UserPrincipal(
      UUID id,
      String fullName,
      String email,
      Boolean active,
      String password,
      String imageUrl,
      List<GrantedAuthority> authorities) {
    this.id = id;
    this.fullName = fullName;
    this.email = email;
    this.active = active;
    this.password = password;
    this.authorities = authorities;
    this.imageUrl = imageUrl;
  }

  public static UserPrincipal create(Account account) {
    List<GrantedAuthority> grantedAuthorities =
        AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + account.getRole().getName());
    return new UserPrincipal(
        account.getId(),
        account.getFullName(),
        account.getEmail(),
        account.isActive(),
        account.getPassword(),
        account.getImageUrl(),
        grantedAuthorities);
  }

  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.active;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserPrincipal that = (UserPrincipal) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {

    return Objects.hash(id);
  }

}
