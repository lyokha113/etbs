package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.RawTemplateVersion;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawTemplateVersionResponse {

  private int id;
  private String name;
  private String content;

  public static RawTemplateVersionResponse setResponse(RawTemplateVersion version){
    return RawTemplateVersionResponse.builder()
        .id(version.getId())
        .name(version.getName())
        .content(version.getContent())
        .build();
  }

}
