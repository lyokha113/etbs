package fpt.capstone.etbs.controller;

import static fpt.capstone.etbs.constant.AppConstant.CURRENT_ONLINE_CACHE;
import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_RAW_QUEUE;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.payload.DesignSessionMessage;
import fpt.capstone.etbs.service.DesignSessionService;
import fpt.capstone.etbs.service.MessagePublish;
import fpt.capstone.etbs.service.RedisService;
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
  private DesignSessionService designSessionService;

  @Autowired
  private RedisService redisService;

  @Autowired
  private MessagePublish messagePublish;

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
      messagePublish.sendOnlineMessage(receiver, dest, currentOnline);
    } else {
      payload.setContributorId(senderId);
      messagePublish.sendOnlineMessage(receiver, dest, payload);
    }

  }

//  @MessageMapping(AppConstant.WEB_SOCKET_CONTENT_APP)
//  public void design(@Payload DesignSessionMessage payload,
//      SimpMessageHeaderAccessor headerAccessor) {
//    String senderId = Objects.requireNonNull(headerAccessor.getUser()).getName();
//    payload.setContributorId(senderId);
//
//    Map<String, String> data = new HashMap<>();
//    data.put("owner", payload.getOwnerId());
//    data.put("content", payload.getContent());
//
//    Map<String, Object> message = new HashMap<>();
//    message.put("data", data);
//    message.put("command", "edit");
//
//    if (!senderId.equals(payload.getOwnerId())) {
//      messagingTemplate.convertAndSendToUser(
//          payload.getOwnerId(),
//          WEB_SOCKET_RAW_QUEUE + payload.getRawId(),
//          message);
//    }
//
//    List<String> contributors = payload.getContributors();
//    if (contributors == null || contributors.isEmpty()) {
//      contributors = designSessionService
//          .getSessionsOfRaw(UUID.fromString(payload.getOwnerId()), payload.getRawId())
//          .stream().map(c -> c.getContributor().getId().toString())
//          .collect(Collectors.toList());
//    }
//
//    contributors.forEach(contributor -> {
//      if (!senderId.equals(contributor)) {
//        messagingTemplate.convertAndSendToUser(
//            contributor,
//            WEB_SOCKET_RAW_QUEUE + payload.getRawId(),
//            message);
//      }
//    });
//
//  }
}
