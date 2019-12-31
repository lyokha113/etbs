package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.MediaFileEditorResponse;
import fpt.capstone.etbs.payload.MediaFileResponse;
import fpt.capstone.etbs.service.MediaFileService;
import fpt.capstone.etbs.util.RoleUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MediaFileController {

  @Autowired
  private MediaFileService mediaFileService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @GetMapping("/file")
  public ResponseEntity<ApiResponse> getMediaFilesOfAccount() {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    List<MediaFile> files = RoleUtils.hasAdminRole(auth) ?
        mediaFileService.getMediaFilesOfAdministrator() :
        mediaFileService.getMediaFilesOfAccount(userPrincipal.getId());
    List<MediaFileResponse> response = files.stream().map(MediaFileResponse::setResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @PostMapping("/file")
  public ResponseEntity<ApiResponse> createMediaFile(@RequestPart MultipartFile[] files)
      throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      List<MediaFile> result = mediaFileService
          .createMediaFiles(userPrincipal.getId(), files);
      List<MediaFileResponse> response = result.stream().map(MediaFileResponse::setResponse)
          .collect(Collectors.toList());
      return ResponseEntity.ok(new ApiResponse<>(true, "Files created", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/file/{id}")
  public ResponseEntity<ApiResponse> changeStatusMediaFile(@PathVariable("id") UUID id,
      @RequestParam("active") boolean active) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      mediaFileService
          .changeActiveMediaFile(userPrincipal.getId(), id, active, RoleUtils.hasAdminRole(auth));
      return ResponseEntity.ok(new ApiResponse<>(true, "File status changed", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @GetMapping("editor/file")
  public ResponseEntity<?> getImagesForEditor() {
    List<MediaFile> files = mediaFileService.getMediaFilesOfAdministrator();
    List<MediaFileEditorResponse> response = files.stream()
        .filter(MediaFile::isActive)
        .map(MediaFileEditorResponse::setResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/editor/file")
  public ResponseEntity<?> createImageFromEditor(@RequestPart MultipartFile [] files)
      throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      List<MediaFile> result = mediaFileService.createMediaFiles(userPrincipal.getId(), files);
      Map<String, String> response = new HashMap<>();
      response.put("link", result.get(0).getLink());
      return ResponseEntity.ok(response);
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @DeleteMapping("editor/file")
  public ResponseEntity<?> deleteImageFromEditor(@RequestParam("src") String src) {
    Authentication auth = authenticationFacade.getAuthentication();
    try {
      if (!src.contains(AppConstant.USER_IMAGES)) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "File not found", null));
      }

      src = src.substring(src.lastIndexOf("/") + 1, src.indexOf("?"));
      UUID id = UUID.fromString(src);
      mediaFileService.changeActiveMediaFile(null, id, false, RoleUtils.hasAdminRole(auth));
      return ResponseEntity.ok(new ApiResponse<>(true, "File status changed", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
