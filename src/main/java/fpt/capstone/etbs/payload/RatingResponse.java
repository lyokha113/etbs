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
  private int vote;
  private int downVote;

  public static RatingResponse setResponse(Rating rating) {
    Template template = rating.getTemplate();
    int vote = (int) template.getRatings().stream().filter(r -> r.isActive() && r.isVote()).count();
    int downVote =
        (int) template.getRatings().stream().filter(r -> r.isActive() && !r.isVote()).count();
    return RatingResponse.builder()
        .templateId(rating.getTemplate().getId())
        .vote(vote)
        .downVote(downVote)
        .build();
  }
}
