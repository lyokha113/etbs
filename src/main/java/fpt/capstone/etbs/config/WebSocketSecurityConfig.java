package fpt.capstone.etbs.config;


import static org.springframework.messaging.simp.SimpMessageType.*;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.RoleEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  @Override
  protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
    messages
//        .nullDestMatcher().authenticated()
        .anyMessage().permitAll();
  }

  @Override
  protected boolean sameOriginDisabled() {
    return true;
  }
}
