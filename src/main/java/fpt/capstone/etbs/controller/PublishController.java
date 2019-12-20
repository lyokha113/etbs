package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.constant.PublishStatus;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.ApprovePublishRequest;
import fpt.capstone.etbs.payload.PublishRequest;
import fpt.capstone.etbs.payload.PublishResponse;
import fpt.capstone.etbs.service.PublishService;
import fpt.capstone.etbs.util.RoleUtils;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublishController {

  @Autowired
  private PublishService publishService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @GetMapping("/publish")
  public ResponseEntity<ApiResponse> getPublishes() {

    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    List<Publish> publishes =
        RoleUtils.hasAdminRole(auth)
            ? publishService.getPublishes()
            : publishService.getPublishes(userPrincipal.getId());
    List<PublishResponse> response =
        publishes.stream().map(PublishResponse::setResponse).collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @PostMapping("/publish")
  public ResponseEntity<ApiResponse> createPublish(
      @Valid @RequestBody PublishRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      Publish publish = publishService.createPublish(userPrincipal.getId(), request);
      publish = publishService.checkDuplicate(publish);
      PublishResponse response = PublishResponse.setResponse(publish);

      if (publish.getStatus().equals(PublishStatus.DENIED)) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Your publish request was denied because of content is quiet similar to other", response));
      } else {
        return ResponseEntity.ok(new ApiResponse<>(true, "Request sent", response));
      }

    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/publish/approve/{id}")
  public ResponseEntity<ApiResponse> approvePublish(
      @PathVariable("id") Integer id,
      @RequestBody ApprovePublishRequest request) throws Exception {
    try {
      Publish publish = publishService.updatePublishStatus(id, PublishStatus.PROCESSING, request.getName());
      PublishResponse response = PublishResponse.setResponse(publish);
      publishService.approve(request, publish);
      return ResponseEntity.ok(new ApiResponse<>(true, "Request sent", response));
    } catch (BadRequestException | IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/publish/deny/{id}")
  public ResponseEntity<ApiResponse> denyPublish(@PathVariable("id") Integer id) {
    try {
      Publish publish = publishService.updatePublishStatus(id, PublishStatus.DENIED, null);
      PublishResponse response = PublishResponse.setResponse(publish);
      return ResponseEntity.ok(new ApiResponse<>(true, "Request sent", response));
    } catch (BadRequestException | IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
