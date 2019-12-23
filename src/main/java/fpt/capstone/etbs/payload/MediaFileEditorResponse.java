package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.MediaFile;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaFileEditorResponse {

  private UUID id;
  private String name;
  private String url;
  private String thumb;

  public static MediaFileEditorResponse setResponse(MediaFile mediaFile) {
    return MediaFileEditorResponse.builder()
        .id(mediaFile.getId())
        .name(mediaFile.getName())
        .url(mediaFile.getLink())
        .build();
  }
}
