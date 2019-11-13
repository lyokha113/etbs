package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.payload.TemplateListByCategories;
import fpt.capstone.etbs.payload.TemplateResponse;
import fpt.capstone.etbs.payload.TemplateUpdateRequest;
import fpt.capstone.etbs.service.TemplateService;
import fpt.capstone.etbs.util.RoleUtils;
import java.util.ArrayList;
import java.util.List;
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
public class TemplateController {

  @Autowired
  private TemplateService templateService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @GetMapping("/template")
  private ResponseEntity<ApiResponse> getTemplates() {

    Authentication auth = authenticationFacade.getAuthentication();
    List<Template> templates =
        RoleUtils.hasAdminRole(auth)
            ? templateService.getTemplates()
            : templateService.getActiveTemplates();

    List<TemplateResponse> response =
        templates.stream().map(TemplateResponse::setResponse).collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @GetMapping("/template/{id}")
  private ResponseEntity<ApiResponse> getTemplate(@PathVariable("id") Integer id) {

    Authentication auth = authenticationFacade.getAuthentication();
    Template response =
        RoleUtils.hasAdminRole(auth)
            ? templateService.getTemplate(id)
            : templateService.getActiveTemplate(id);

    return response != null
        ? ResponseEntity.ok(
        new ApiResponse<>(true, "", TemplateResponse.setResponseWithContent(response)))
        : ResponseEntity.badRequest().body(new ApiResponse<>(true, "Not found", null));
  }

  @PostMapping("/template")
  private ResponseEntity<ApiResponse> createTemplate(
      @Valid @RequestBody TemplateCreateRequest request) throws Exception {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      Template template = templateService.createTemplate(userPrincipal.getId(), request);
      template = templateService.updateThumbnail(template, request.getRawTemplateId());
      TemplateResponse response = TemplateResponse.setResponse(template);
      return ResponseEntity.ok(new ApiResponse<>(true, "Template created", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/template/{id}")
  private ResponseEntity<ApiResponse> updateTemplate(
      @PathVariable("id") Integer id,
      @Valid @RequestBody TemplateUpdateRequest request) throws Exception {
    try {
      Template template = templateService.updateTemplate(id, request);
      TemplateResponse response = TemplateResponse.setResponse(template);
      return ResponseEntity.ok(new ApiResponse<>(true, "Template updated", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @DeleteMapping("/template/{id}")
  public ResponseEntity<ApiResponse> deleteWorkspace(@PathVariable("id") int id) {
    try {
      templateService.deleteTemplate(id);
      return ResponseEntity.ok(new ApiResponse<>(true, "Template deleted", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @GetMapping("/template/list")
  public ResponseEntity<ApiResponse> getTemplateByCategories(
      @Valid @RequestBody TemplateListByCategories categories) {
    try {
      List<Template> templateList = templateService.getListByCategories(categories);
      List<TemplateResponse> response = new ArrayList<>();
      for (Template template : templateList) {
        response.add(TemplateResponse.setResponseWithContent(template));
      }
      return ResponseEntity.ok(new ApiResponse<>(true, "Get template list successful", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
