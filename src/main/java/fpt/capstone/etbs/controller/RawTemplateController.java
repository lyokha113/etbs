package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateCreateResponse;
import fpt.capstone.etbs.service.RawTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class RawTemplateController {
    @Autowired
    RawTemplateService rawTemplateService;

    @PostMapping("/rawtemplate")
    private ResponseEntity<ApiResponse> createRawTemplate(
            @Valid @RequestBody RawTemplateCreateRequest request) {

        RawTemplateCreateResponse rawTemplateResponse = rawTemplateService.createTemplate(request);
        return rawTemplateResponse != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Template created", rawTemplateResponse)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Template can not create", null));
    }
}
