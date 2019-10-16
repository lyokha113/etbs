package fpt.capstone.etbs.payload;


import fpt.capstone.etbs.model.Category;
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
public class CategoryResponse {

    private int id;
    private String name;
    private List<Template> templates;

    public static CategoryResponse setResponse(Category category) {
        List<Template> templates = category.getTemplates()
                .stream()
                .map(t -> Template.builder().id(t.getId()).name(t.getName()).build())
                .collect(Collectors.toList());

        return CategoryResponse.builder().id(category.getId()).name(category.getName()).templates(templates).build();
    }



}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Template {
    private int id;
    private String name;
}
