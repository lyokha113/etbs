package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.constant.NotificationCode;
import fpt.capstone.etbs.constant.NotificationStatus;
import fpt.capstone.etbs.model.DesignSession;
import fpt.capstone.etbs.model.Notification;
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
  private String content;
  private String icon;
  private NotificationStatus status;
  private NotificationCode code;

  public static NotificationResponse setResponse(Notification notification) {
    return NotificationResponse.builder()
        .id(notification.getId())
        .content(notification.getContent())
        .icon(notification.getIcon())
        .status(notification.getStatus())
        .code(notification.getCode())
        .build();
  }
}
