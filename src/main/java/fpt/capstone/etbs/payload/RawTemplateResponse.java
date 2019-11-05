package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.RawTemplateVersion;
import java.util.List;
import java.util.stream.Collectors;
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
  private String content;
  private String description;
  private VersionOfTemplate currentVersion;
  private List<VersionOfTemplate> versions;

  public static RawTemplateResponse setResponse(RawTemplate rawTemplate) {
    return RawTemplateResponse.builder()
        .id(rawTemplate.getId())
        .name(rawTemplate.getName())
        .thumbnail(rawTemplate.getCurrentVersion().getThumbnail())
        .workspaceId(rawTemplate.getWorkspace().getId())
        .description(rawTemplate.getDescription())
        .currentVersion(VersionOfTemplate.setResponse(rawTemplate.getCurrentVersion()))
        .versions(rawTemplate.getVersions().stream()
            .map(VersionOfTemplate::setResponse)
            .collect(Collectors.toList()))
        .build();
  }

  public static RawTemplateResponse setResponseWithContent(RawTemplate rawTemplate) {
    RawTemplateResponse response = RawTemplateResponse.setResponse(rawTemplate);
    response.setContent(rawTemplate.getCurrentVersion().getContent());
    return response;
  }


}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class VersionOfTemplate {

  private int id;
  private String name;

  public static VersionOfTemplate setResponse(RawTemplateVersion rawTemplateVersion) {
    return VersionOfTemplate.builder()
        .id(rawTemplateVersion.getId())
        .name(rawTemplateVersion.getName())
        .build();
  }
}
