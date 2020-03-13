package fpt.capstone.etbs.service.impl;

import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_INVITATION_QUEUE;
import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_PUBLISH_QUEUE;
import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_PUBLISH_TOPIC;
import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_USER_EMAIL_QUEUE;

import fpt.capstone.etbs.service.MessagePublisherService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisherServiceImpl implements MessagePublisherService {

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
  public void sendAddInvitation(String receiver, Object data) {
    Map<String, Object> message = new HashMap<>();
    message.put("command", "add");
    message.put("data", data);
    messagingTemplate.convertAndSendToUser(receiver, WEB_SOCKET_INVITATION_QUEUE, message);
  }

  @Override
  public void sendRemoveInvitation(String receiver, Object data) {
    Map<String, Object> message = new HashMap<>();
    message.put("command", "remove");
    message.put("data", data);
    messagingTemplate.convertAndSendToUser(receiver, WEB_SOCKET_INVITATION_QUEUE, message);
  }

  @Override
  public void sendUserEmailApproved(String receiver, Object data) {
    messagingTemplate.convertAndSendToUser(receiver, WEB_SOCKET_USER_EMAIL_QUEUE, data);
  }

  @Override
  public void sendDesignContent(String receiver, String dest, Object data) {
    Map<String, Object> message = new HashMap<>();
    message.put("command", "content");
    message.put("data", data);
    messagingTemplate.convertAndSendToUser(receiver, dest, message);
  }

  @Override
  public void sendDesignFiles(String receiver, String dest, Object data) {
    Map<String, Object> message = new HashMap<>();
    message.put("command", "file");
    message.put("data", data);
    messagingTemplate.convertAndSendToUser(receiver, dest, message);
  }

  @Override
  public void sendPublishesAdmin(Object data) {
    messagingTemplate.convertAndSend(WEB_SOCKET_PUBLISH_TOPIC, data);
  }

  @Override
  public void sendPublishes(String receiver, Object data) {
    messagingTemplate.convertAndSendToUser(receiver, WEB_SOCKET_PUBLISH_QUEUE, data);
  }
}
