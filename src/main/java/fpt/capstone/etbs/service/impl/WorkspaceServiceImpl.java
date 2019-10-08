package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.WorkspaceCreateRequest;
import fpt.capstone.etbs.payload.WorkspaceStatusRequest;
import fpt.capstone.etbs.payload.WorkspaceUpdateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.WorkspaceRepository;
import fpt.capstone.etbs.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    WorkspaceRepository workspaceRepository;
    @Autowired
    AccountRepository accountRepository;

    @Override
    public Workspace createWorkspace(WorkspaceCreateRequest request) {
        Workspace workspace = new Workspace();
        if (accountRepository.findById(request.getId_user()).isPresent()) {
            workspace.setAccount(accountRepository.findById(request.getId_user()).get());
            workspace.setName(request.getName());
        }
        return workspace;
    }

    @Override
    public Workspace getWorkspace(int id) {
        return workspaceRepository.findById(id).orElse(null);
    }

    @Override
    public Workspace updateWorkspace(WorkspaceUpdateRequest request) {
        Workspace workspace = getWorkspace(request.getId());
        if (workspace != null) {
            workspace.setName(request.getName());
        }
        return workspace;
    }

    @Override
    public Workspace changeStatusWorkspace(WorkspaceStatusRequest request) {
        Workspace workspace = getWorkspace(request.getId());
        if (workspace != null) {
            workspace.setActive(request.isActive());
        }
        return workspace;
    }
}
