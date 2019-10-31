package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.Tutorial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  public static TutorialResponse setResponse(Tutorial tutorial) {
    return TutorialResponse.builder()
        .id(tutorial.getId())
        .name(tutorial.getName())
        .thumbnail(tutorial.getThumbnail())
        .description(tutorial.getDescription())
        .active(tutorial.isActive())
        .build();
  }

  public static TutorialResponse setResponseWithContent(Tutorial tutorial) {
    TutorialResponse response = TutorialResponse.setResponse(tutorial);
    response.setContent(tutorial.getContent());
    return response;
  }
}
