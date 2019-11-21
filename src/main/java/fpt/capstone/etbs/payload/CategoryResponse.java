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
  private boolean active;

  public static CategoryResponse setResponse(Category category) {
    Set<Template> templates = category.getTemplates();
    List<TemplateOfCategory> templatesOfCategory =
        templates == null
            ? new ArrayList<>()
            : templates.stream().map(TemplateOfCategory::setResponse).collect(Collectors.toList());
    return CategoryResponse.builder()
        .id(category.getId())
        .name(category.getName())
        .templates(templatesOfCategory)
        .active(category.isActive())
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
  private String description;
  private int upVote;
  private int downVote;
  private boolean active;

  static TemplateOfCategory setResponse(Template template) {
    int upVote = (int) template.getRatings().stream().filter(Rating::isVote).count();
    int downVote =
        (int) template.getRatings().stream().filter(r -> !r.isVote()).count();
    return TemplateOfCategory.builder()
        .id(template.getId())
        .name(template.getName())
        .authorName(template.getAuthor().getFullName())
        .authorId(template.getAuthor().getId().toString())
        .thumbnail(template.getThumbnail())
        .description(template.getDescription())
        .upVote(upVote)
        .downVote(downVote)
        .build();
  }
}
