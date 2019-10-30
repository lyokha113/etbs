package fpt.capstone.etbs.payload;

import java.util.List;
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
  private String thumbnail;
  private String content;
  private String description;
  private List<CategoriesOfTemplate> categories;
}
