package fpt.capstone.etbs.service;

public interface MessagePublish {

  void sendOnlineMessage(String receiver, String dest, Object data);

  void sendLeaveSession(String receiver, String dest, Object data);

  void sendKickSession(String receiver, String dest);

  void sendRemoveInvitation(String receiver, Object data);
}
