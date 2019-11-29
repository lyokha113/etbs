package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.constant.PublishStatus;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.payload.AccountCreateRequest;
import fpt.capstone.etbs.payload.AccountResponse;
import fpt.capstone.etbs.payload.AccountUpdateRequest;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.PublishRequest;
import fpt.capstone.etbs.payload.PublishResponse;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.service.PublishService;
import fpt.capstone.etbs.service.TemplateService;
import fpt.capstone.etbs.util.RoleUtils;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublishController {

  @Autowired
  private PublishService publishService;

  @Autowired
  private TemplateService templateService;

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
      Publish publish = publishService.publish(userPrincipal.getId(), request);
      publish = publishService.checkDuplicate(publish);
      PublishResponse response = PublishResponse.setResponse(publish);

      if (publish.getStatus().equals(PublishStatus.DENIED)) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "DUPLICATED", response));
      }
      return ResponseEntity.ok(new ApiResponse<>(true, "Request sent", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/publish/{id}")
  public ResponseEntity<ApiResponse> updatePublish(
      @PathVariable("id") Integer id,
      @RequestBody TemplateCreateRequest request) {
    try {
      Publish publish = publishService.updatePublish(id, request);
      if (publish.getStatus().equals(PublishStatus.PUBLISHED)) {
        Template template = templateService.createTemplate(request);
        template = templateService.updateContentImage(template);
        template = templateService.updateThumbnail(template);
      }
      PublishResponse response = PublishResponse.setResponse(publish);
      return ResponseEntity.ok(new ApiResponse<>(true, "Request sent", response));
    } catch (BadRequestException | IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    } catch (Exception ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
