package fpt.capstone.etbs.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class LoginResponse {

  private String accessToken;

  public LoginResponse(String accessToken) {
    this.accessToken = "Bearer " + accessToken;
  }
}
