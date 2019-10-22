package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateCreateRequest {
    //TODO: add link thumpnail
    private String name;
    private String content;
    private String description;
    private UUID author;
    private List<Integer> categories;

}
