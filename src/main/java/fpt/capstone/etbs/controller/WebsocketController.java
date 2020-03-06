package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.payload.DesignSessionMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

  @MessageMapping(AppConstant.WEB_SOCKET_CONTRIBUTOR_APP)
  public boolean online(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
    System.out.println(message);
    System.out.println("connected");
    return true;
  }
}
