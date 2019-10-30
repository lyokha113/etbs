package fpt.capstone.etbs.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DraftEmailCreateRequest {

  private String email;
  private String password;
  private String subject;
  private String body;
  private String provider;
}
