package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.constant.PublishStatus;
import fpt.capstone.etbs.model.Publish;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublishResponse {

  private Integer id;
  private String name;
  private String description;
  private String content;
  private String authorName;
  private UUID authorId;
  private PublishStatus status;
  private Double duplicateRate;
  private String duplicateName;
  private String duplicateContent;
  private String requestDate;

  public static PublishResponse setResponse(Publish publish) {
    return PublishResponse.builder()
        .id(publish.getId())
        .authorId(publish.getAuthor().getId())
        .authorName(publish.getAuthor().getFullName())
        .name(publish.getName())
        .description(publish.getDescription())
        .content(publish.getContent())
        .status(publish.getStatus())
        .duplicateRate(publish.getDuplicateRate() == null ? 0.0 : publish.getDuplicateRate())
        .duplicateName(
            publish.getDuplicateTemplate() == null ? "" : publish.getDuplicateTemplate().getName())
        .duplicateContent(publish.getDuplicateTemplate() == null ? ""
            : publish.getDuplicateTemplate().getContent())
        .requestDate(publish.getCreatedDate().toString())
        .build();
  }
}
