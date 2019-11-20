package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.MediaFileResponse;
import fpt.capstone.etbs.payload.MediaFileUpdateRequest;
import fpt.capstone.etbs.payload.UploadFile;
import fpt.capstone.etbs.service.MediaFileService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    List<MediaFile> files = mediaFileService.getMediaFilesOfAccount(userPrincipal.getId());
    List<MediaFileResponse> response =
        files.stream().map(MediaFileResponse::setResponse).collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @GetMapping("/file/{id}")
  public ResponseEntity<ApiResponse> getMediaFileOfAccount(
      @PathVariable("id") UUID id) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    MediaFile response = mediaFileService.getMediaFileOfAccount(id, userPrincipal.getId());
    return response != null
        ? ResponseEntity.ok(new ApiResponse<>(true, "", MediaFileResponse.setResponse(response)))
        : ResponseEntity.badRequest().body(new ApiResponse<>(true, "Not found", null));
  }

  @PostMapping("/file")
  public ResponseEntity<ApiResponse> createMediaFile(@ModelAttribute UploadFile file)
      throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    UUID id = UUID.randomUUID();
    try {
      MediaFile mediaFile =
          mediaFileService.createMediaFile(userPrincipal.getId(), id, file.getName(), file.getFile());
      MediaFileResponse response = MediaFileResponse.setResponse(mediaFile);
      return ResponseEntity.ok(new ApiResponse<>(true, "File created", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/file/{id}")
  public ResponseEntity<ApiResponse> updateMediaFile(
      @PathVariable("id") UUID id,
      @Valid @RequestBody MediaFileUpdateRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      MediaFile mediaFile = mediaFileService.updateMediaFile(userPrincipal.getId(), id, request);
      MediaFileResponse response = MediaFileResponse.setResponse(mediaFile);
      return ResponseEntity.ok(new ApiResponse<>(true, "File updated", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @DeleteMapping("/file/{id}")
  public ResponseEntity<ApiResponse> deleteMediaFile(
      @PathVariable("id") UUID id) {
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
