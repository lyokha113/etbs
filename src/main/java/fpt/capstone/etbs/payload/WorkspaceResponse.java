package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        List<RawTemplateResponse> rawTemplates = workspace.getRawTemplates().stream()
                .map(RawTemplateResponse::setResponse)
                .collect(Collectors.toList());

        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .userId(workspace.getAccount().getId())
                .name(workspace.getName())
                .rawTemplates(rawTemplates)
                .build();
    }
}
