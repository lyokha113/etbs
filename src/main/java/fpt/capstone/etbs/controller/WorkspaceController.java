package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.WorkspaceCreateRequest;
import fpt.capstone.etbs.payload.WorkspaceStatusRequest;
import fpt.capstone.etbs.payload.WorkspaceUpdateRequest;
import fpt.capstone.etbs.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/workspace")
public class WorkspaceController {
    @Autowired
    WorkspaceService workspaceService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createWorkspace(@Valid @RequestBody WorkspaceCreateRequest request) {
        Workspace workspace = workspaceService.createWorkspace(request);
        return workspace != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "The workspace created", workspace)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "The workspace can not created!!", null));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateMediaFile(@Valid @RequestBody WorkspaceUpdateRequest request) {
        Workspace workspace = workspaceService.updateWorkspace(request);
        return workspace != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "The workspace updated", workspace)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "The workspace can not be updated!!", null));
    }
    @PutMapping("/status")
    public ResponseEntity<ApiResponse> changeStatusMediaFile(@Valid @RequestBody WorkspaceStatusRequest request) {
        Workspace workspace = workspaceService.changeStatusWorkspace(request);
        return workspace != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "The workspace file updated", workspace)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "The workspace file can not be updated!!", null));
    }
}
