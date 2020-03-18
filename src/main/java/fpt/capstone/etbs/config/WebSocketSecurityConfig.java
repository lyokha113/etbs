package fpt.capstone.etbs.config;


import static fpt.capstone.etbs.constant.AppConstant.*;
import static fpt.capstone.etbs.constant.RoleEnum.*;
import static org.springframework.messaging.simp.SimpMessageType.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  @Override
  protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
    messages
        .simpTypeMatchers(CONNECT, UNSUBSCRIBE, DISCONNECT).permitAll()
        .simpDestMatchers("/app/**").hasRole(USER.getName())
        .simpSubscribeDestMatchers("/queue/**").hasRole(USER.getName())
        .simpSubscribeDestMatchers("/topic/**").hasRole(ADMINISTRATOR.getName());
  }

  @Override
  protected boolean sameOriginDisabled() {
    return true;
  }
}
