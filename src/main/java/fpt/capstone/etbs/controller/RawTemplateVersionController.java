package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.RawTemplateVersion;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.RawTemplateVersionRequest;
import fpt.capstone.etbs.payload.RawTemplateVersionResponse;
import fpt.capstone.etbs.service.RawTemplateVersionService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RawTemplateVersionController {

  @Autowired
  private RawTemplateVersionService rawTemplateVersionService;

  @PostMapping("/version")
  private ResponseEntity<ApiResponse> createVersion(
      Authentication auth,
      @Valid @RequestBody RawTemplateVersionRequest request) {

    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      RawTemplateVersion version = rawTemplateVersionService
          .createVersion(userPrincipal.getId(), request);
      RawTemplateVersionResponse response = RawTemplateVersionResponse.setResponse(version);
      return ResponseEntity.ok(new ApiResponse<>(true, "Version created", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/version/{id}")
  private ResponseEntity<ApiResponse> updateVersion(
      Authentication auth,
      @PathVariable("id") Integer id,
      @Valid @RequestBody RawTemplateVersionRequest request) {

    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      RawTemplateVersion version = rawTemplateVersionService
          .updateVersion(userPrincipal.getId(), id, request);
      RawTemplateVersionResponse response = RawTemplateVersionResponse.setResponse(version);
      return ResponseEntity.ok(new ApiResponse<>(true, "Version updated", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @DeleteMapping("/version/{id}")
  public ResponseEntity<ApiResponse> deleteVersion(
      Authentication auth, @PathVariable("id") int id) {
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      rawTemplateVersionService.deleteVersion(userPrincipal.getId(), id);
      return ResponseEntity.ok(new ApiResponse<>(true, "Version deleted", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

}
