package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.MediaFileResponse;
import fpt.capstone.etbs.service.MediaFileService;
import fpt.capstone.etbs.util.RoleUtils;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    List<MediaFile> files = RoleUtils.hasAdminRole(auth)
        ? mediaFileService.getMediaFilesOfAccount(userPrincipal.getId())
        : mediaFileService.getMediaFilesOfAdministrator();
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

  @DeleteMapping("/file/{id}")
  public ResponseEntity<ApiResponse> deleteMediaFile(@PathVariable("id") UUID id) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      mediaFileService.deactivateMediaFile(userPrincipal.getId(), id);
      return ResponseEntity.ok(new ApiResponse<>(true, "File deleted", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
