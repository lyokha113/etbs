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
public class MediaFileResponse {

  private UUID id;
  private String name;
  private String link;
  private boolean active;
  private boolean open;
  private UUID accountId;

  public static MediaFileResponse setResponse(MediaFile mediaFile) {
    return MediaFileResponse.builder()
        .id(mediaFile.getId())
        .name(mediaFile.getName())
        .link(mediaFile.getLink())
        .accountId(mediaFile.getAccount().getId())
        .active(mediaFile.isActive())
        .open(mediaFile.isOpen())
        .build();
  }
}
