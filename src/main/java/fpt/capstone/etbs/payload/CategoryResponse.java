package fpt.capstone.etbs.payload;


import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.Template;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                .map(TemplateOfCategory::setTemplateOfCategory)
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
    private String thumbnail;
    private int upVote;
    private int downVote;
    private boolean active;

    static TemplateOfCategory setTemplateOfCategory(Template template) {
        int upVote = (int) template.getRatings().stream().filter(Rating::isVote).count();
        int downVote = (int) template.getRatings().stream().filter(r -> !r.isVote()).count();
        return TemplateOfCategory
                .builder()
                .id(template.getId())
                .name(template.getName())
                .authorName(template.getAuthor().getFullName())
                .thumbnail(template.getThumbnail())
                .upVote(upVote)
                .downVote(downVote)
                .build();
    }

}
