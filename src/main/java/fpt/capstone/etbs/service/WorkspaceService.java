package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.WorkspaceCreateRequest;
import fpt.capstone.etbs.payload.WorkspaceStatusRequest;
import fpt.capstone.etbs.payload.WorkspaceUpdateRequest;

public interface WorkspaceService {
    Workspace createWorkspace(WorkspaceCreateRequest request);
    Workspace getWorkspace(int id);
    Workspace updateWorkspace(WorkspaceUpdateRequest request);
    Workspace changeStatusWorkspace(WorkspaceStatusRequest request);
}
