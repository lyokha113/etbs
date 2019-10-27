package fpt.capstone.etbs.payload;

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
public class TemplateUpdateRequest {
    //TODO: add link thumpnail
    private String name;
    private String content;
    private String description;
    private String thumpnail;
    private UUID author;
    private List<Integer> categories;
    private boolean active;
}
