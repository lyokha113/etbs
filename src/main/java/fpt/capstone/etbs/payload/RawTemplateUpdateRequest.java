package fpt.capstone.etbs.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawTemplateUpdateRequest {

  private String name;
  private String description;
  private String content;
  private MultipartFile thumbnail;
}
