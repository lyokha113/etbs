package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.*;
import fpt.capstone.etbs.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping("/template")
    private ResponseEntity<ApiResponse> getAllTemplate() {
        List<TemplateResponse> template = templateService.getAllListTemplate();
        return template != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Get template successful", template)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Get template failed. Not found", null));
    }

    @GetMapping("/template/{id}")
    private ResponseEntity<ApiResponse> getTemplate(@PathVariable("id") int id) {
        TemplateResponse template = templateService.getTemplate(id);
        return template != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Get template successful", template)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Get template failed. Not found", null));
    }

    @GetMapping("/template/author/{uuid}")
    private ResponseEntity<ApiResponse> getTemplateList(@PathVariable("uuid") UUID id) {
        List<TemplateResponse> templateList = templateService.getListTemplate(id);
        return templateList != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Get template successful", templateList)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Get template failed. Not found", null));
    }

    @PutMapping("/template/{id}")
    private ResponseEntity<ApiResponse> updateTemplate(
            @PathVariable("id") int id,
            @Valid @RequestBody TemplateUpdateRequest request) {
        boolean check = templateService.updateTemplate(id, request);
        return !check ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Update template successful", check)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Update template failed. Not found", null));
    }

    @PostMapping("/template")
    private ResponseEntity<ApiResponse> createTemplate(
            @Valid @RequestBody TemplateCreateRequest request) {
        TemplateCreateResponse response = templateService.createTemplate(request);
        return response != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Template created", response)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Template name is duplicated", null));
    }
}
