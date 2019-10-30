package fpt.capstone.etbs.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtAuthenticationResponse {

  private String accessToken;

  public JwtAuthenticationResponse(String accessToken) {
    this.accessToken = "Bearer " + accessToken;
  }

}
