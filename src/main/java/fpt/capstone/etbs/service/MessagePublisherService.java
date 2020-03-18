package fpt.capstone.etbs.service;

public interface MessagePublisherService {

  void sendOnlineMessage(String receiver, String dest, Object data);

  void sendLeaveSession(String receiver, String dest, Object data);

  void sendKickSession(String receiver, String dest);

  void sendAddInvitation(String receiver, Object data);

  void sendRemoveInvitation(String receiver, Object data);

  void sendUserEmailApproved(String receiver, Object data);

  void sendDesignContent(String receiver, String dest, Object data);

  void sendDesignFiles(String receiver, String dest, Object data);

  void sendPublishes(Object data);

  void sendPublish(String receiver, Object data);

  void sendUpdatePublish(String receiver, Object data);

  void sendNotification(String receiver, Object data);
}
