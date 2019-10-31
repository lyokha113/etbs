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
public class CategoryResponse {

  private int id;
  private String name;
  private List<TemplateOfCategory> templates;

  public static CategoryResponse setResponse(Category category) {
    Set<Template> templates = category.getTemplates();
    List<TemplateOfCategory> templatesOfCategory = templates == null ? new ArrayList<>() :
        templates.stream()
            .map(TemplateOfCategory::setResponse)
            .collect(Collectors.toList());
    return CategoryResponse
        .builder()
        .id(category.getId())
        .name(category.getName())
        .templates(templatesOfCategory)
        .build();
  }
}


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TemplateOfCategory {

  private int id;
  private String name;
  private String authorName;
  private String authorId;
  private String thumbnail;
  private int like;
  private int dislike;
  private boolean active;

  static TemplateOfCategory setResponse(Template template) {
    int like = (int) template.getRatings().stream().filter(r -> r.isActive() && r.isLike()).count();
    int dislike = (int) template.getRatings().stream().filter(r -> r.isActive() && !r.isLike()).count();
    return TemplateOfCategory
        .builder()
        .id(template.getId())
        .name(template.getName())
        .authorName(template.getAuthor().getFullName())
        .authorId(template.getAuthor().getId().toString())
        .thumbnail(template.getThumbnail())
        .like(like)
        .dislike(dislike)
        .build();
  }

}
