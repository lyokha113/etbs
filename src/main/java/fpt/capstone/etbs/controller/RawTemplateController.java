package fpt.capstone.etbs.controller;

import static fpt.capstone.etbs.constant.AppConstant.CURRENT_ONLINE_CACHE;
import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_RAW_QUEUE;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.MediaFileResponse;
import fpt.capstone.etbs.payload.RawTemplateRequest;
import fpt.capstone.etbs.payload.RawTemplateResponse;
import fpt.capstone.etbs.payload.SaveContentRequest;
import fpt.capstone.etbs.service.MessagePublisherService;
import fpt.capstone.etbs.service.RawTemplateService;
import fpt.capstone.etbs.service.RedisService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class RawTemplateController {

  @Autowired
  private RawTemplateService rawTemplateService;

  @Autowired
  private RedisService redisService;

  @Autowired
  private MessagePublisherService messagePublisherService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @GetMapping("/raw/{id}")
  public ResponseEntity<?> getRawTemplate(@PathVariable("id") Integer id) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    RawTemplate response = rawTemplateService.getRawTemplate(id, userPrincipal.getId());
    return response != null
        ? ResponseEntity.ok(
        new ApiResponse<>(true, "", RawTemplateResponse.setResponseWithContent(response)))
        : ResponseEntity.badRequest().body(new ApiResponse<>(true, "Not found", null));
  }

  @PostMapping("/raw")
  private ResponseEntity<?> createRawTemplate(
      @Valid @RequestBody RawTemplateRequest request) throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      RawTemplate raw = rawTemplateService.createRawTemplate(userPrincipal.getId(), request);
      RawTemplateResponse response = RawTemplateResponse.setResponse(raw);
      return ResponseEntity.ok(new ApiResponse<>(true, "Raw template created", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/raw/{id}")
  private ResponseEntity<?> updateRawTemplate(
      @PathVariable("id") Integer id,
      @Valid @RequestBody RawTemplateRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      RawTemplate template = rawTemplateService
          .updateRawTemplate(userPrincipal.getId(), id, request);
      RawTemplateResponse response = RawTemplateResponse.setResponse(template);
      return ResponseEntity.ok(new ApiResponse<>(true, "Raw template updated", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PatchMapping("/raw/{id}/content")
  private ResponseEntity<?> updateContent(
      @PathVariable("id") Integer id,
      @Valid @RequestBody SaveContentRequest request) throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      RawTemplate template = StringUtils.isEmpty(request.getContributor()) ?
        rawTemplateService.updateContent(userPrincipal.getId(), id, request.getContent()) :
        rawTemplateService.updateContentContributor(UUID.fromString(request.getContributor()), id, request.getContent());
      if (request.isAutoSave()) {
        String currentOnlineKey = CURRENT_ONLINE_CACHE + id;
        String dest = WEB_SOCKET_RAW_QUEUE + id;

        Set<Object> currentOnline = redisService.getOnlineSessions(currentOnlineKey);
        currentOnline.forEach(user -> {
          String receiverId = String.valueOf(user);
          if (!userPrincipal.getId().toString().equals(receiverId)) {
            messagePublisherService.sendDesignContent(receiverId, dest, request.getContent());
          }
        });
      } else {
        rawTemplateService.updateThumbnail(template);
      }
      RawTemplateResponse response = RawTemplateResponse.setResponseWithContent(template);
      return ResponseEntity.ok(new ApiResponse<>(true, "Raw template content changed", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PatchMapping("/raw/{id}/file")
  private ResponseEntity<?> uploadFiles(
      @PathVariable("id") Integer id,
      @RequestPart MultipartFile[] files) throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      List<MediaFile> uploaded = rawTemplateService.uploadFiles(userPrincipal.getId(), id, files);
      return ResponseEntity.ok(new ApiResponse<>(true, "Files was uploaded",
          MediaFileResponse.setResponse(uploaded.get(0))));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @DeleteMapping("/raw/{id}")
  public ResponseEntity<?> deleteRawTemplate(
      @PathVariable("id") int id) throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      rawTemplateService.deleteRawTemplate(userPrincipal.getId(), id);
      return ResponseEntity.ok(new ApiResponse<>(true, "Raw template deleted", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
