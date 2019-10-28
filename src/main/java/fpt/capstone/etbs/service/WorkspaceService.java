package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.WorkspaceRequest;

import java.util.List;
import java.util.UUID;

public interface WorkspaceService {
    List<Workspace> getWorkspacesOfAccount(UUID accountId);
    Workspace createWorkspace(UUID accountId, WorkspaceRequest request);
    Workspace updateWorkspace(UUID accountId, int workspaceId, WorkspaceRequest request);
    void deleteWorkspace(UUID accountId, int workspaceId);
}
