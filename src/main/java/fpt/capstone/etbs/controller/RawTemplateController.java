package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateCreateResponse;
import fpt.capstone.etbs.payload.RawTemplateResponse;
import fpt.capstone.etbs.service.RawTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class RawTemplateController {
    @Autowired
    RawTemplateService rawTemplateService;

    @PostMapping("/rawtemplate")
    private ResponseEntity<ApiResponse> createRawTemplate(
            Authentication auth,
            @Valid @RequestBody RawTemplateCreateRequest request) {

        RawTemplateCreateResponse rawTemplateResponse = rawTemplateService.createTemplate(request);
        return rawTemplateResponse != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Template created", rawTemplateResponse)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Template can not create", null));
    }

    @GetMapping("/rawtemplate/workspace/{wId}")
    private ResponseEntity<ApiResponse> getRawTemplateByWorkspaceId(
            Authentication auth,
            @Valid @PathVariable int wId) {

        List<RawTemplateResponse> rawTemplateResponse = rawTemplateService.getTemplateByWorkspaceId(wId);
        return rawTemplateResponse != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Templates", rawTemplateResponse)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Do not contain any templates", null));
    }

    @GetMapping("/rawtemplate/{id}")
    private ResponseEntity<ApiResponse> getRawTemplateById(
            Authentication auth,
            @Valid @PathVariable int id
    ) {
        RawTemplate rawTemplate = rawTemplateService.getRawTemplateById(id);
        RawTemplateResponse response = new RawTemplateResponse();
        if (rawTemplate != null) {
            response.setId(id);
            response.setContent(rawTemplate.getContent());
            response.setDescription(rawTemplate.getDescription());
            response.setName(rawTemplate.getName());
            response.setWorkspaceId(rawTemplate.getWorkspace().getId());
            response.setThumbnail(rawTemplate.getThumbnail());
        }
        return response != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Templates", response)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Do not contain any templates", null));
    }
}
