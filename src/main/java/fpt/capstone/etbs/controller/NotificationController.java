package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Notification;
import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.Tutorial;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.NotificationResponse;
import fpt.capstone.etbs.payload.RatingRequest;
import fpt.capstone.etbs.payload.RatingResponse;
import fpt.capstone.etbs.payload.TutorialResponse;
import fpt.capstone.etbs.service.NotificationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private AuthenticationFacade authenticationFacade;


  @GetMapping("/notification")
  private ResponseEntity<?> getNotifications(@RequestParam("page") int page) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    Slice<Notification> slice = notificationService.getNotifications(userPrincipal.getId(), page);
    List<Notification> notifications = slice.getContent();
    Map<String, Object> response = new HashMap<>();
    response.put("next", slice.hasNext());
    response.put("notifications", notifications.stream()
        .map(NotificationResponse::setResponse)
        .collect(Collectors.toList()));
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @PostMapping("/notification")
  private ResponseEntity<?> loadNotification(@RequestParam("ids") Integer [] ids) {
    try {
      notificationService.loadNotification(ids);
      return ResponseEntity.ok(new ApiResponse<>(true, "", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
