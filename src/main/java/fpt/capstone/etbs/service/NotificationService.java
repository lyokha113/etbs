package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  List<Notification> getNotifications(UUID accountId);

  List<Notification> getUnloadNotifications(UUID accountId);

  List<Notification> getNotificationsToRemove();

  void createPublishNotification();

  void createApproveNotification(Account receiver, Integer templateId);

  void createDenyNotification(Account receiver);

  void createEmailNotification(Account receiver);

  void createInvitationNotification(Account receiver);

  void createKickNotification(Account receiver);

  void createLeaveNotification(Account receiver);

  void loadNotification(Integer id);
}
