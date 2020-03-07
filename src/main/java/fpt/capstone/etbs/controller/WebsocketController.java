package fpt.capstone.etbs.controller;

import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_RAW_QUEUE;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.payload.DesignSessionMessage;
import fpt.capstone.etbs.service.AccountService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @MessageMapping(AppConstant.WEB_SOCKET_CONTRIBUTOR_APP)
  public void online(@Payload DesignSessionMessage payload, SimpMessageHeaderAccessor headerAccessor) {
    String senderId = Objects.requireNonNull(headerAccessor.getUser()).getName();
    payload.setContributorId(senderId);

    Map<String, Object> message = new HashMap<>();
    message.put("data", payload);
    message.put("command", "edit");
    messagingTemplate.convertAndSendToUser(payload.getOwnerId(), WEB_SOCKET_RAW_QUEUE + payload.getRawId(), message);
  }
}
