package fpt.capstone.etbs.controller;

import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_RAW_QUEUE;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.DesignSession;
import fpt.capstone.etbs.payload.DesignSessionMessage;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.DesignSessionService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpSubscriptionMatcher;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @Autowired
  private DesignSessionService designSessionService;

  @MessageMapping(AppConstant.WEB_SOCKET_CONTRIBUTOR_APP)
  public void online(@Payload DesignSessionMessage payload,
      SimpMessageHeaderAccessor headerAccessor) {
    String senderId = Objects.requireNonNull(headerAccessor.getUser()).getName();
    payload.setContributorId(senderId);

    Map<String, Object> message = new HashMap<>();
    message.put("data", payload);
    message.put("command", "contributor");
    messagingTemplate
        .convertAndSendToUser(payload.getOwnerId(), WEB_SOCKET_RAW_QUEUE + payload.getRawId(),
            message);
  }

  @MessageMapping(AppConstant.WEB_SOCKET_CONTENT_APP)
  public void design(@Payload DesignSessionMessage payload,
      SimpMessageHeaderAccessor headerAccessor) {
    String senderId = Objects.requireNonNull(headerAccessor.getUser()).getName();
    payload.setContributorId(senderId);

    Map<String, String> data = new HashMap<>();
    data.put("owner", payload.getOwnerId());
    data.put("content", payload.getContent());

    Map<String, Object> message = new HashMap<>();
    message.put("data", data);
    message.put("command", "edit");

    if (!senderId.equals(payload.getOwnerId())) {
      messagingTemplate.convertAndSendToUser(
          payload.getOwnerId(),
          WEB_SOCKET_RAW_QUEUE + payload.getRawId(),
          message);
    }

    List<String> contributors = payload.getContributors();
    if (contributors == null || contributors.isEmpty()) {
      contributors = designSessionService
          .getSessionsOfRaw(UUID.fromString(payload.getOwnerId()), payload.getRawId())
          .stream().map(c -> c.getContributor().getId().toString())
          .collect(Collectors.toList());
    }

    contributors.forEach(contributor -> {
      if (!senderId.equals(contributor)) {
        messagingTemplate.convertAndSendToUser(
            contributor,
            WEB_SOCKET_RAW_QUEUE + payload.getRawId(),
            message);
      }
    });

  }
}
