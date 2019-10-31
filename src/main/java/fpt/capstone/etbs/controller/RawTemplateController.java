package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateResponse;
import fpt.capstone.etbs.service.RawTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class RawTemplateController {

  @Autowired private RawTemplateService rawTemplateService;

  @GetMapping("/raw/{id}")
  public ResponseEntity<ApiResponse> getRawTemplate(
      Authentication auth, @PathVariable("id") Integer id) {
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    RawTemplate response = rawTemplateService.getRawTemplate(id, userPrincipal.getId());
    return response != null
        ? ResponseEntity.ok(
            new ApiResponse<>(true, "", RawTemplateResponse.setResponseWithContent(response)))
        : ResponseEntity.badRequest().body(new ApiResponse<>(true, "Not found", null));
  }

  @PostMapping("/raw")
  private ResponseEntity<ApiResponse> createRawTemplate(
      Authentication auth, @Valid @RequestBody RawTemplateCreateRequest request) {

    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      RawTemplate template = rawTemplateService.createRawTemplate(userPrincipal.getId(), request);
      RawTemplateResponse response = RawTemplateResponse.setResponse(template);
      return ResponseEntity.ok(new ApiResponse<>(true, "Raw template created", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
  //
  //    @GetMapping("/raw/workspace/{wId}")
  //    private ResponseEntity<ApiResponse> getRawTemplateByWorkspaceId(
  //            Authentication auth,
  //            @Valid @PathVariable int wId) {
  //
  //        List<RawTemplateResponse> rawTemplateResponse =
  // rawTemplateService.getTemplateByWorkspaceId(wId);
  //        return rawTemplateResponse != null ?
  //                ResponseEntity.ok(
  //                        new ApiResponse<>(true, "Templates", rawTemplateResponse)) :
  //                ResponseEntity.badRequest().body(
  //                        new ApiResponse<>(false, "Do not contain any templates", null));
  //    }
  //
  //    @GetMapping("/raw/{id}")
  //    private ResponseEntity<ApiResponse> getRawTemplateById(
  //            Authentication auth,
  //            @Valid @PathVariable int id
  //    ) {
  //        RawTemplate rawTemplate = rawTemplateService.getRawTemplateById(id);
  //        RawTemplateResponse response = new RawTemplateResponse();
  //        if (rawTemplate != null) {
  //            response.setId(id);
  //            response.setContent(rawTemplate.getContent());
  //            response.setDescription(rawTemplate.getDescription());
  //            response.setName(rawTemplate.getName());
  //            response.setWorkspaceId(rawTemplate.getWorkspace().getId());
  //            response.setThumbnail(rawTemplate.getThumbnail());
  //        }
  //        return response != null ?
  //                ResponseEntity.ok(
  //                        new ApiResponse<>(true, "Templates", response)) :
  //                ResponseEntity.badRequest().body(
  //                        new ApiResponse<>(false, "Do not contain any templates", null));
  //    }
}
