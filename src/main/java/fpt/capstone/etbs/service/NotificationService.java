package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Notification;
import fpt.capstone.etbs.payload.NotificationRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Slice;

public interface NotificationService {

  Slice<Notification> getNotifications(UUID accountId, int currentPage);

  List<Notification> getLoadedNotifications();

  void createNotification(Account account, NotificationRequest request);

  void loadNotification(Integer [] ids);
}
