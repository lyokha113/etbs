package fpt.capstone.etbs.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawTemplateRequest {

  private String name;
  private String description;
  private Integer templateId;
  private Integer workspaceId;
}
