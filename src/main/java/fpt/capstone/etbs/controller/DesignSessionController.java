package fpt.capstone.etbs.controller;


import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.DesignSession;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.DesignSessionRequest;
import fpt.capstone.etbs.payload.DesignSessionResponse;
import fpt.capstone.etbs.payload.MediaFileResponse;
import fpt.capstone.etbs.payload.StringWrapperRequest;
import fpt.capstone.etbs.service.DesignSessionService;
import fpt.capstone.etbs.service.MediaFileService;
import fpt.capstone.etbs.service.RawTemplateService;
import java.util.Comparator;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DesignSessionController {

  @Autowired
  private DesignSessionService designSessionService;

  @Autowired
  private RawTemplateService rawTemplateService;

  @Autowired
  private MediaFileService mediaFileService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @GetMapping("/session/raw/{id}")
  public ResponseEntity<?> getContributors(@PathVariable("id") Integer id) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    List<DesignSession> sessions = designSessionService.getSessionsOfRaw(userPrincipal.getId(), id);
    List<DesignSessionResponse> response = sessions.stream()
        .map(DesignSessionResponse::setResponseContributor)
        .collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @PostMapping("/session/raw")
  public ResponseEntity<?> createContributor(@Valid @RequestBody DesignSessionRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      DesignSession session = designSessionService
          .createDesignSession(userPrincipal.getId(), request);
      return ResponseEntity
          .ok(new ApiResponse<>(true, "", DesignSessionResponse.setResponseContributor(session)));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/session/raw/{rawId}")
  public ResponseEntity<?> kickContributors(@PathVariable("rawId") Integer rawId) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      designSessionService.deleteSession(userPrincipal.getId(), rawId);
      return ResponseEntity.ok(new ApiResponse<>(true, "Session was closed", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }

  }

  @PutMapping("/session/raw/{rawId}/{contributorId}")
  public ResponseEntity<?> kickContributor(
      @PathVariable("rawId") Integer rawId,
      @PathVariable("contributorId") UUID contributorId) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      designSessionService.deleteSession(userPrincipal.getId(), contributorId, rawId);
      return ResponseEntity.ok(new ApiResponse<>(true, "Sessions were closed", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }


  @GetMapping("/session/user")
  public ResponseEntity<?> getSessionsForUser() {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    List<DesignSession> sessions = designSessionService.getSessionsForUser(userPrincipal.getId());
    List<DesignSessionResponse> response = sessions.stream()
        .map(DesignSessionResponse::setResponse)
        .sorted(Comparator.comparing(DesignSessionResponse::getInvitedDate).reversed())
        .collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @GetMapping("/session/user/{rawId}")
  public ResponseEntity<?> getSessionForUser(@PathVariable("rawId") Integer rawId) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    DesignSession session = designSessionService.getSessionForUser(userPrincipal.getId(), rawId);
    if (session != null) {
      List<MediaFile> files = mediaFileService
          .getOwnerSessionMediaFiles(session.getRawTemplate().getWorkspace().getAccount().getId());
      return ResponseEntity.ok(new ApiResponse<>(true, "",
          DesignSessionResponse.setResponseWithContent(session, files)));
    } else {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Session not found", null));
    }

  }

  @PutMapping("/session/user/{rawId}")
  public ResponseEntity<?> leaveSession(
      @PathVariable("rawId") Integer rawId) throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      designSessionService.leaveSession(userPrincipal.getId(), rawId);
      return ResponseEntity.ok(new ApiResponse<>(true, "Session was close", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/session/user/{rawId}/content")
  public ResponseEntity<?> updateContent(
      @PathVariable("rawId") Integer rawId,
      @Valid @RequestBody StringWrapperRequest request) throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      RawTemplate rawTemplate = designSessionService
          .updateContent(userPrincipal.getId(), rawId, request.getString());
      rawTemplateService.updateThumbnail(rawTemplate);
      return ResponseEntity.ok(new ApiResponse<>(true, "Template was updated", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/session/user/{rawId}/file")
  public ResponseEntity<?> uploadFileToOwner(
      @PathVariable("rawId") Integer rawId,
      @RequestPart MultipartFile[] files) throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      List<MediaFile> uploaded = designSessionService
          .uploadFileToOwner(userPrincipal.getId(), rawId, files);
      return ResponseEntity.ok(new ApiResponse<>(true, "Files was uploaded",
          MediaFileResponse.setResponse(uploaded.get(0))));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }


}
