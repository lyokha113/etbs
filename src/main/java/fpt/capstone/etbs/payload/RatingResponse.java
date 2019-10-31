package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.Template;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {

  private int templateId;
  private int like;
  private int dislike;

  public static RatingResponse setResponse(Rating rating) {
    Template template = rating.getTemplate();
    int like = (int) template.getRatings().stream().filter(r -> r.isActive() && r.isLike()).count();
    int dislike =
        (int) template.getRatings().stream().filter(r -> r.isActive() && !r.isLike()).count();
    return RatingResponse.builder()
        .templateId(rating.getTemplate().getId())
        .like(like)
        .dislike(dislike)
        .build();
  }
}
