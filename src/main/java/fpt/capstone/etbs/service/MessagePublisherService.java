package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Notification;
import java.util.List;

public interface MessagePublisherService {

  void sendOnlineMessage(String receiver, String dest, Object data);

  void sendLeaveSession(String receiver, String dest, Object data);

  void sendKickSession(String receiver, String dest);

  void sendAddInvitation(String receiver, Object data);

  void sendRemoveInvitation(String receiver, Object data);

  void sendUserEmailApproved(String receiver, Object data);

  void sendDesignContent(String receiver, String dest, String data);

  void sendDesignFiles(String receiver, String dest, Object data);

  void sendPublishes(Object data);

  void sendPublish(String receiver, Object data);

  void sendUpdatePublish(String receiver, Object data);

  void sendNotification(String receiver, Object data);

  void sendNotifications(List<Notification> data);


}
