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
  private String email;
  private Boolean status;

  public static UserEmailResponse setResponse(UserEmail userEmail) {
    return UserEmailResponse.builder()
        .id(userEmail.getId())
        .email(userEmail.getEmail())
        .status(userEmail.getStatus())
        .build();
  }
}
