package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.service.TemplateService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateResponse  {

  private int id;
  private String name;
  private String authorName;
  private String authorId;
  private String authorAvatar;
  private String content;
  private String thumbnail;
  private int upVote;
  private int downVote;
  private String description;
  private double score;
  private List<CategoryOfTemplate> categories;

  public static TemplateResponse setResponse(Template template) {
    Set<Category> categories = template.getCategories();
    List<CategoryOfTemplate> categoryOfTemplates =
        categories == null
            ? new ArrayList<>()
            : categories.stream().map(CategoryOfTemplate::setResponse).collect(Collectors.toList());
    Set<Rating> ratings = template.getRatings();
    int up = ratings != null ? (int) template.getRatings().stream().filter(Rating::isVote).count() : 0;
    int down = ratings != null ? (int) template.getRatings().stream().filter(r -> !r.isVote()).count() : 0;
    return TemplateResponse.builder()
        .id(template.getId())
        .name(template.getName())
        .authorName(template.getAuthor().getFullName())
        .authorId(template.getAuthor().getId().toString())
        .authorAvatar(template.getAuthor().getImageUrl())
        .thumbnail(template.getThumbnail())
        .upVote(up)
        .downVote(down)
        .score(TemplateService.calculateScore(up, down, template.getCreatedDate()))
        .description(template.getDescription())
        .categories(categoryOfTemplates)
        .build();
  }

  public static TemplateResponse setResponseWithContent(Template template) {
    TemplateResponse response = TemplateResponse.setResponse(template);
    response.setContent(template.getContent());
    return response;
  }

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CategoryOfTemplate {

  private int id;
  private String name;

  public static CategoryOfTemplate setResponse(Category category) {
    return CategoryOfTemplate.builder().id(category.getId()).name(category.getName()).build();
  }
}
