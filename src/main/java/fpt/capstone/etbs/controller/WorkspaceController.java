package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.WorkspaceRequest;
import fpt.capstone.etbs.payload.WorkspaceResponse;
import fpt.capstone.etbs.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class WorkspaceController {

    @Autowired
    WorkspaceService workspaceService;

    @GetMapping("/workspace")
    public ResponseEntity<ApiResponse> getWorkspacesOfAccount(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        List<Workspace> workspaces = workspaceService.getWorkspacesOfAccount(userPrincipal.getId());
        List<WorkspaceResponse> response = workspaces.stream()
                .map(WorkspaceResponse::setResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "", response));
    }

    @PostMapping("/workspace")
    public ResponseEntity<ApiResponse> createWorkspace(
            Authentication auth,
            @Valid @RequestBody WorkspaceRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        try {
            Workspace workspace = workspaceService.createWorkspace(userPrincipal.getId(), request);
            WorkspaceResponse response = WorkspaceResponse.setResponse(workspace);
            return ResponseEntity.ok(new ApiResponse<>(true, "Workspace created", response));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
        }

    }

    @PutMapping("/workspace/{id}")
    public ResponseEntity<ApiResponse> updateWorkspace(
            Authentication auth,
            @PathVariable("id") int id,
            @Valid @RequestBody WorkspaceRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        try {
            Workspace workspace = workspaceService.updateWorkspace(userPrincipal.getId(), id, request);
            WorkspaceResponse response = WorkspaceResponse.setResponse(workspace);
            return ResponseEntity.ok(new ApiResponse<>(true, "Workspace updated", response));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
        }

    }

    @DeleteMapping("/workspace/{id}")
    public ResponseEntity<ApiResponse> deleteWorkspace(
            Authentication auth,
            @PathVariable("id") int id) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        try {
            workspaceService.deleteWorkspace(userPrincipal.getId(), id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Workspace deleted", null));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
        }

    }
}
