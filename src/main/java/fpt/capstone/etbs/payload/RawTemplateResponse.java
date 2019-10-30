package fpt.capstone.etbs.payload;


import fpt.capstone.etbs.model.RawTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawTemplateResponse {

  private Integer id;
  private Integer workspaceId;
  private String name;
  private String thumbnail;

  public static RawTemplateResponse setResponse(RawTemplate rawTemplate) {
    return RawTemplateResponse.builder()
        .id(rawTemplate.getId())
        .name(rawTemplate.getName())
        .thumbnail(rawTemplate.getThumbnail())
        .workspaceId(rawTemplate.getWorkspace().getId())
        .build();
  }
}
