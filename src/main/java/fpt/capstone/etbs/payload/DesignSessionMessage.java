package fpt.capstone.etbs.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesignSessionMessage {

  private Integer rawId;
  private String ownerId;
  private String contributorId;
  private Boolean online;
  private String content;
}
