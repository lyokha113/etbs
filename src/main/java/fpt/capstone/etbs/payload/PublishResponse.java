package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.constant.PublishStatus;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Publish;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublishResponse {
  private Integer id;
  private String content;
  private String authorName;
  private UUID authorId;
  private PublishStatus status;
  private Double duplicateRate;
  private String duplicateContent;

  public static PublishResponse setResponse(Publish publish) {
    return PublishResponse.builder()
        .id(publish.getId())
        .authorId(publish.getAuthor().getId())
        .authorName(publish.getAuthor().getFullName())
        .content(publish.getContent())
        .status(publish.getStatus())
        .duplicateRate(publish.getDuplicateRate())
        .duplicateContent(publish.getDuplicateContent())
        .build();
  }
}
