package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Notification;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.NotificationResponse;
import fpt.capstone.etbs.service.NotificationService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private AuthenticationFacade authenticationFacade;


  @GetMapping("/notification")
  private ResponseEntity<?> getUnloadNotifications() {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    List<Notification> notifications = notificationService
        .getUnloadNotifications(userPrincipal.getId());
    List<NotificationResponse> response = notifications.stream()
        .map(NotificationResponse::setResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @GetMapping("/notification/all")
  private ResponseEntity<?> getAllNotifications() {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    List<Notification> notifications = notificationService.getNotifications(userPrincipal.getId());
    List<NotificationResponse> response = notifications.stream()
        .map(NotificationResponse::setResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @PostMapping("/notification/{id}")
  private ResponseEntity<?> loadNotification(@PathVariable("id") Integer id) {
    try {
      notificationService.loadNotification(id);
      return ResponseEntity.ok(new ApiResponse<>(true, "", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
