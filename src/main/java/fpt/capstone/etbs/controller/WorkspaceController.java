package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.WorkspaceRequest;
import fpt.capstone.etbs.payload.WorkspaceResponse;
import fpt.capstone.etbs.service.WorkspaceService;
import java.util.List;
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
public class WorkspaceController {

  @Autowired
  private WorkspaceService workspaceService;

  @GetMapping("/workspace")
  public ResponseEntity<ApiResponse> getWorkspacesOfAccount(Authentication auth) {
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    List<Workspace> workspaces = workspaceService.getWorkspacesOfAccount(userPrincipal.getId());
    List<WorkspaceResponse> response =
        workspaces.stream().map(WorkspaceResponse::setResponse).collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @GetMapping("/workspace/{id}")
  public ResponseEntity<ApiResponse> getWorkspaceOfAccount(
      Authentication auth, @PathVariable("id") Integer id) {
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    Workspace response = workspaceService.getWorkspaceOfAccount(id, userPrincipal.getId());
    return response != null
        ? ResponseEntity.ok(new ApiResponse<>(true, "", WorkspaceResponse.setResponse(response)))
        : ResponseEntity.badRequest().body(new ApiResponse<>(true, "Not found", null));
  }

  @PostMapping("/workspace")
  public ResponseEntity<ApiResponse> createWorkspace(
      Authentication auth, @Valid @RequestBody WorkspaceRequest request) {
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
      Authentication auth, @PathVariable("id") int id) {
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      workspaceService.deleteWorkspace(userPrincipal.getId(), id);
      return ResponseEntity.ok(new ApiResponse<>(true, "Workspace deleted", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
