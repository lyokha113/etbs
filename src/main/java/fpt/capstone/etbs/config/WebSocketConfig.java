package fpt.capstone.etbs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOrigins("*");
    registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
  }

//  @Override
//  public void configureClientInboundChannel(ChannelRegistration registration) {
//    registration.interceptors(new ChannelInterceptor() {
//
//      @Override
//      public Message<?> preSend(@NotNull Message<?> message, MessageChannel channel) {
//
//        StompHeaderAccessor accessor =
//            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//          String token = accessor.getFirstNativeHeader("Token");
//          if (!StringUtils.isEmpty(token)) {
//            List<GrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//            Authentication auth = new UsernamePasswordAuthenticationToken(user, user, authorities);
//            SecurityContextHolder.getContext().setAuthentication(auth);
//            accessor.setUser(auth);
//          }
//        }
//
//        return message;
//      }
//    });
//  }
}
