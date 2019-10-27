package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.Template;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateResponse {
    private int id;
    private String name;
    private String thumpnail;
    private String content;
    private String description;
    private List<Category> categories;

    public static TemplateResponse setResponse(Template template) {
        List<Category> categories = template.getCategories()
                .stream()
                .map(category ->
                        Category.builder().id(category.getId()).name(category.getName()).build())
                .collect(Collectors.toList());
        return TemplateResponse.builder().id(template.getId()).name(template.getName()).categories(categories).build();
    }

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Category {
    private int id;
    private String name;
}
