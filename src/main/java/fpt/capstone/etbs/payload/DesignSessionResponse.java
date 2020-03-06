package fpt.capstone.etbs.payload;


import fpt.capstone.etbs.model.DesignSession;
import fpt.capstone.etbs.model.MediaFile;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesignSessionResponse {

  private UUID contributorId;
  private String contributorEmail;
  private UUID ownerId;
  private String ownerName;
  private String ownerEmail;
  private List<MediaFileResponse> files;
  private Integer rawId;
  private String rawName;
  private String rawContent;
  private String rawThumbnail;
  private String rawDescription;
  private String modifiedDate;
  private String invitedDate;

  public static DesignSessionResponse setResponse(DesignSession session) {
    return DesignSessionResponse.builder()
        .ownerId(session.getRawTemplate().getWorkspace().getAccount().getId())
        .ownerName(session.getRawTemplate().getWorkspace().getAccount().getFullName())
        .ownerEmail(session.getRawTemplate().getWorkspace().getAccount().getEmail())
        .rawId(session.getRawTemplate().getId())
        .rawName(session.getRawTemplate().getName())
        .rawThumbnail(session.getRawTemplate().getThumbnail())
        .rawDescription(session.getRawTemplate().getDescription())
        .modifiedDate(session.getRawTemplate().getLastModifiedDate().toString())
        .invitedDate(session.getCreatedDate().toString())
        .build();
  }

  public static DesignSessionResponse setResponseContributor(DesignSession session) {
    return DesignSessionResponse.builder()
        .contributorId(session.getContributor().getId())
        .contributorEmail(session.getContributor().getEmail())
        .build();
  }

  public static DesignSessionResponse setResponseWithContent(DesignSession session,
      List<MediaFile> files) {
    DesignSessionResponse response = DesignSessionResponse.setResponse(session);
    response.setRawContent(session.getRawTemplate().getContent());
    response
        .setFiles(files.stream().map(MediaFileResponse::setResponse).collect(Collectors.toList()));
    return response;
  }
}
