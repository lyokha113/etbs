package fpt.capstone.etbs.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateUpdateRequest {

  private String name;
  private String description;
  private String content;
  private MultipartFile thumbnail;
  private List<Integer> categoryIds;
  private boolean active;
}
