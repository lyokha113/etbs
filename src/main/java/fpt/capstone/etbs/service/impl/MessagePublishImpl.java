package fpt.capstone.etbs.service.impl;

import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_INVITATION_QUEUE;

import fpt.capstone.etbs.service.MessagePublish;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class MessagePublishImpl implements MessagePublish {

  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @Override
  public void sendOnlineMessage(String receiver, String dest, Object data) {
    Map<String, Object> message = new HashMap<>();
    message.put("command", "contributor");
    message.put("data", data);
    messagingTemplate.convertAndSendToUser(receiver, dest, message);
  }

  @Override
  public void sendLeaveSession(String receiver, String dest, Object data) {
    Map<String, Object> message = new HashMap<>();
    message.put("command", "leave");
    message.put("data", data);
    messagingTemplate.convertAndSendToUser(receiver, dest, message);
  }

  @Override
  public void sendKickSession(String receiver, String dest) {
    Map<String, Object> message = new HashMap<>();
    message.put("command", "kick");
    messagingTemplate.convertAndSendToUser(receiver, dest, message);
  }

  @Override
  public void sendRemoveInvitation(String receiver, Object data) {
    Map<String, Object> message = new HashMap<>();
    message.put("command", "remove");
    message.put("data", data);
    messagingTemplate.convertAndSendToUser(receiver, WEB_SOCKET_INVITATION_QUEUE, message);
  }
}
