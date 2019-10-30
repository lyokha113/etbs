package fpt.capstone.etbs.payload;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateCreateRequest {

  private String name;
  private String content;
  private String description;
  private UUID author;
  private List<Integer> categories;

}
