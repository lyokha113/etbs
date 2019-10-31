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

  private UUID rawTemplateId;
  private String name;
  private String description;
  private List<Integer> categoryIds;
}
