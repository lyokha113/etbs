package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.Workspace;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceResponse {

  private Integer id;
  private UUID userId;
  private String name;
  private List<RawTemplateResponse> rawTemplates;

  public static WorkspaceResponse setResponse(Workspace workspace) {

    Set<RawTemplate> rawTemplates = workspace.getRawTemplates();
    List<RawTemplateResponse> rawTemplatesResponse =
        rawTemplates == null
            ? new ArrayList<>()
            : rawTemplates.stream()
                .map(RawTemplateResponse::setResponse)
                .collect(Collectors.toList());

    return WorkspaceResponse.builder()
        .id(workspace.getId())
        .userId(workspace.getAccount().getId())
        .name(workspace.getName())
        .rawTemplates(rawTemplatesResponse)
        .build();
  }
}
