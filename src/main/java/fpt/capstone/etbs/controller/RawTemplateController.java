package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateResponse;
import fpt.capstone.etbs.payload.RawTemplateUpdateRequest;
import fpt.capstone.etbs.service.RawTemplateService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RawTemplateController {

  @Autowired
  private RawTemplateService rawTemplateService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @GetMapping("/raw/{id}")
  public ResponseEntity<ApiResponse> getRawTemplate(@PathVariable("id") Integer id) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    RawTemplate response = rawTemplateService.getRawTemplate(id, userPrincipal.getId());
    return response != null
        ? ResponseEntity.ok(
        new ApiResponse<>(true, "", RawTemplateResponse.setResponseWithContent(response)))
        : ResponseEntity.badRequest().body(new ApiResponse<>(true, "Not found", null));
  }

  @PostMapping("/raw")
  private ResponseEntity<ApiResponse> createRawTemplate(
      @Valid @RequestBody RawTemplateCreateRequest request)
      throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      RawTemplate template = rawTemplateService.createRawTemplate(userPrincipal.getId(), request);
      if (request.getTemplateId() != null) {
        template = rawTemplateService
            .updateRawTemplate(request.getTemplateId(), template);
      }
      RawTemplateResponse response = RawTemplateResponse.setResponseWithContent(template);
      return ResponseEntity.ok(new ApiResponse<>(true, "Raw template created", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/raw/{id}")
  private ResponseEntity<ApiResponse> updateRawTemplate(
      @PathVariable("id") Integer id,
      @Valid @RequestBody RawTemplateUpdateRequest request)
      throws Exception {
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

  @PutMapping("/raw/{id}/version/{vid}")
  private ResponseEntity<ApiResponse> changeVersion(

      @PathVariable("id") Integer id,
      @PathVariable("vid") Integer versionId) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      RawTemplate template = rawTemplateService.changeVersion(userPrincipal.getId(), id, versionId);
      RawTemplateResponse response = RawTemplateResponse.setResponseWithContent(template);
      return ResponseEntity.ok(new ApiResponse<>(true, "Raw template version changed", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @DeleteMapping("/raw/{id}")
  public ResponseEntity<ApiResponse> deleteRawTemplate(
      @PathVariable("id") int id) {
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
