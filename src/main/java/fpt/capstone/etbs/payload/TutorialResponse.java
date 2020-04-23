package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.Tutorial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorialResponse {

  private Integer id;
  private String name;
  private String description;
  private String thumbnail;
  private String content;
  private boolean active;
  private long date;

  public static TutorialResponse setResponse(Tutorial tutorial) {
    long time = LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
            - tutorial.getLastModifiedDate().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    return TutorialResponse.builder()
        .id(tutorial.getId())
        .name(tutorial.getName())
        .thumbnail(tutorial.getThumbnail())
        .description(tutorial.getDescription())
        .active(tutorial.isActive())
        .date(time)
        .build();
  }

  public static TutorialResponse setResponseWithContent(Tutorial tutorial) {
    TutorialResponse response = TutorialResponse.setResponse(tutorial);
    response.setContent(tutorial.getContent());
    return response;
  }
}
