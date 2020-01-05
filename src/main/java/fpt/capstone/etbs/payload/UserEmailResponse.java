package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.UserEmail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailResponse {

  private Integer id;
  private String name;
  private String email;
  private String status;
  private String provider;

  public static UserEmailResponse setResponse(UserEmail userEmail) {
    return UserEmailResponse.builder()
        .id(userEmail.getId())
        .email(userEmail.getEmail())
        .name(userEmail.getName())
        .status(userEmail.getStatus())
        .provider(userEmail.getProvider())
        .build();
  }
}
