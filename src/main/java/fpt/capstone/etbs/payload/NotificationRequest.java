package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.constant.NotificationCode;
import fpt.capstone.etbs.constant.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

  private String content;
  private String icon;
  private NotificationStatus status;
  private NotificationCode code;
}
