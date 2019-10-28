package fpt.capstone.etbs.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawTemplateCreateRequest {
    private String content;
    private String description;
    private String name;
    private int workspaceId;
    private String thumbnail;
}
