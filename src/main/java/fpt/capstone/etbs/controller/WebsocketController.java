package fpt.capstone.etbs.controller;

import static fpt.capstone.etbs.constant.AppConstant.CURRENT_ONLINE_CACHE;
import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_RAW_QUEUE;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.payload.DesignSessionMessage;
import fpt.capstone.etbs.service.MessagePublisherService;
import fpt.capstone.etbs.service.RedisService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

  @Autowired
  private RedisService redisService;

  @Autowired
  private MessagePublisherService messagePublisherService;

  @MessageMapping(AppConstant.WEB_SOCKET_CONTRIBUTOR_APP)
  public void online(@Payload DesignSessionMessage payload,
      SimpMessageHeaderAccessor headerAccessor) {

    String senderId = Objects.requireNonNull(headerAccessor.getUser()).getName();
    String currentOnlineKey = CURRENT_ONLINE_CACHE + payload.getRawId();
    if (payload.getOnline()) {
      redisService.setOnlineSession(currentOnlineKey, senderId);
    } else {
      redisService.setOfflineSession(currentOnlineKey, senderId);
    }

    String receiver = payload.getOwnerId();
    String dest = WEB_SOCKET_RAW_QUEUE + payload.getRawId();
    if (senderId.equals(payload.getOwnerId())) {
      Set<Object> currentOnline = redisService.getOnlineSessions(currentOnlineKey);
      messagePublisherService.sendOnlineMessage(receiver, dest, currentOnline);
    } else {
      payload.setContributorId(senderId);
      messagePublisherService.sendOnlineMessage(receiver, dest, payload);
    }

  }

  @MessageMapping(AppConstant.WEB_SOCKET_CONTENT_APP)
  public void design(@Payload DesignSessionMessage payload,
      SimpMessageHeaderAccessor headerAccessor) {
    String senderId = Objects.requireNonNull(headerAccessor.getUser()).getName();
    payload.setContributorId(senderId);

    String currentOnlineKey = CURRENT_ONLINE_CACHE + payload.getRawId();
    String dest = WEB_SOCKET_RAW_QUEUE + payload.getRawId();

    Set<Object> currentOnline = redisService.getOnlineSessions(currentOnlineKey);
    currentOnline.forEach(user -> {
      String receiverId = String.valueOf(user);
      if (!senderId.equals(receiverId)) {
        messagePublisherService.sendDesignContent(receiverId, dest, payload.getContent());
      }
    });
  }
}
