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
public class TutorialRequest {

  private String name;
  private String content;
  private String description;
  private MultipartFile thumbnail;
  private boolean active;
}
