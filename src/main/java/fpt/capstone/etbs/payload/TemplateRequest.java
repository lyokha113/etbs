package fpt.capstone.etbs.payload;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {

  private String name;
  private String description;
  private String content;
  private UUID authorId;
  private List<Integer> categoryIds;

  public TemplateRequest(ApprovePublishRequest approveRequest, String content, UUID authorId) {
    this.name = approveRequest.getName();
    this.description = approveRequest.getDescription();
    this.content = content;
    this.authorId = authorId;
    this.categoryIds = approveRequest.getCategoryIds();
  }
}
