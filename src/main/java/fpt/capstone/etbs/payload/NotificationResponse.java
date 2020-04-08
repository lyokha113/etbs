package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.constant.NotificationStatus;
import fpt.capstone.etbs.model.Notification;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

  private Integer id;
  private String title;
  private String content;
  private boolean loaded;
  private NotificationStatus status;
  private String code;
  private long createdDate;

  public static NotificationResponse setResponse(Notification notification) {
    long time = LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
        - notification.getCreatedDate().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    return NotificationResponse.builder()
        .id(notification.getId())
        .title(notification.getTitle())
        .content(notification.getContent())
        .loaded(notification.isLoaded())
        .status(notification.getStatus())
        .code(notification.getCode())
        .createdDate(time)
        .build();
  }
}
