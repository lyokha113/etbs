package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.Template;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateResponse {

  private int id;
  private String name;
  private String authorName;
  private String authorId;
  private String content;
  private String thumbnail;
  private int upVote;
  private int downVote;
  private boolean active;
  private String description;
  private List<CategoryOfTemplate> categories;

  public static TemplateResponse setResponse(Template template) {
    Set<Category> categories = template.getCategories();
    List<CategoryOfTemplate> categoryOfTemplates =
        categories == null
            ? new ArrayList<>()
            : categories.stream().map(CategoryOfTemplate::setResponse).collect(Collectors.toList());
    int upVote = 0;
    int downVote = 0;
    if (template.getRatings() != null) {
      upVote = (int) template.getRatings().stream().filter(Rating::isVote).count();
      downVote =
          (int) template.getRatings().stream().filter(r -> !r.isVote()).count();
    }
    return TemplateResponse.builder()
        .id(template.getId())
        .name(template.getName())
        .authorName(template.getAuthor().getFullName())
        .authorId(template.getAuthor().getId().toString())
        .thumbnail(template.getThumbnail())
        .upVote(upVote)
        .downVote(downVote)
        .active(template.isActive())
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
