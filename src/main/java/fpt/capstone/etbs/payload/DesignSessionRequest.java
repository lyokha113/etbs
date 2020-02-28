package fpt.capstone.etbs.payload;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesignSessionRequest {

  private String contributorEmail;
  private Integer rawId;
}
