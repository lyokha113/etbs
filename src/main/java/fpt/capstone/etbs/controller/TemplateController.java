package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.TemplateRequest;
import fpt.capstone.etbs.payload.TemplateResponse;
import fpt.capstone.etbs.service.TemplateService;
import fpt.capstone.etbs.util.RoleUtils;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
public class TemplateController {

  @Autowired
  private TemplateService templateService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @GetMapping("/template")
  private ResponseEntity<?> getTemplates() {
    Authentication auth = authenticationFacade.getAuthentication();
    List<Template> templates =
        RoleUtils.hasAdminRole(auth)
            ? templateService.getTemplates()
            : templateService.getTemplatesForUser();
    List<TemplateResponse> response =
        templates.stream()
            .sorted(Comparator.comparingDouble(Template::getScore)
                .thenComparing(Template::getCreatedDate).reversed())
            .map(TemplateResponse::setResponse)
            .collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @GetMapping("/template/{id}")
  private ResponseEntity<?> getTemplate(@PathVariable("id") Integer id) {
    Template response = templateService.getTemplate(id);
    return response != null
        ? ResponseEntity
        .ok(new ApiResponse<>(true, "", TemplateResponse.setResponseWithContent(response)))
        : ResponseEntity.badRequest().body(new ApiResponse<>(true, "Not found", null));
  }

  @GetMapping("/template/author/{uuid}")
  private ResponseEntity<?> getTemplateByAuthor(@PathVariable("uuid") UUID uuid) {
    List<Template> templates = templateService.getTemplatesByAuthor(uuid);
    List<TemplateResponse> response =
        templates.stream()
            .sorted(Comparator.comparingDouble(Template::getScore)
                .thenComparing(Template::getCreatedDate).reversed())
            .map(TemplateResponse::setResponse)
            .collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @PostMapping("/template")
  private ResponseEntity<?> createTemplate(@RequestBody TemplateRequest request)
      throws Exception {
    try {
      Template template = templateService.createTemplate(request);
      template = templateService.updateThumbnail(template);
      TemplateResponse response = TemplateResponse.setResponse(template);
      return ResponseEntity.ok(new ApiResponse<>(true, "Template created", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/template/{id}")
  private ResponseEntity<?> updateTemplate(
      @PathVariable("id") Integer id,
      @Valid @RequestBody TemplateRequest request) throws Exception {
    try {
      Template template = templateService.updateTemplate(id, request);
      TemplateResponse response = TemplateResponse.setResponse(template);
      return ResponseEntity.ok(new ApiResponse<>(true, "Template updated", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @DeleteMapping("/template/{id}")
  public ResponseEntity<?> deleteTemplate(@PathVariable("id") int id) throws Exception {
    try {
      templateService.deleteTemplate(id);
      return ResponseEntity.ok(new ApiResponse<>(true, "Template deleted", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
