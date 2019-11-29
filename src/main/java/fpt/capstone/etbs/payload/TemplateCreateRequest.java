package fpt.capstone.etbs.payload;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateCreateRequest {

  private String name;
  private String description;
  private String content;
  private UUID authorId;
  private List<Integer> categoryIds;
  private String status;

}
