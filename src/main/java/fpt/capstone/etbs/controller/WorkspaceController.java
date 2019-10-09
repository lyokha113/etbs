package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.WorkspaceCreateRequest;
import fpt.capstone.etbs.payload.WorkspaceUpdateRequest;
import fpt.capstone.etbs.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class WorkspaceController {

    @Autowired
    WorkspaceService workspaceService;

    @PostMapping("/workspace")
    public ResponseEntity<ApiResponse> createWorkspace(
            @Valid @RequestBody WorkspaceCreateRequest request) {
        Workspace workspace = workspaceService.createWorkspace(request);
        return workspace != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "The workspace created", workspace)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "The workspace can not created!!", null));
    }

    @PutMapping("/workspace/{id}")
    public ResponseEntity<ApiResponse> updateWorkspace(
            @PathVariable("id") int id,
            @Valid @RequestBody WorkspaceUpdateRequest request) {
        Workspace workspace = workspaceService.updateWorkspace(id, request);
        return workspace != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "The workspace updated", workspace)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "The workspace can not be updated!!", null));
    }
}
