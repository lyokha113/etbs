package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.WorkspaceRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.WorkspaceRepository;
import fpt.capstone.etbs.service.WorkspaceService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

  @Autowired
  private WorkspaceRepository workspaceRepository;
  @Autowired
  private AccountRepository accountRepository;
  @Autowired
  private RawTemplateRepository rawTemplateRepository;

  @Override
  public List<Workspace> getWorkspacesOfAccount(UUID accountId) {
    return workspaceRepository.getByAccount_Id(accountId);
  }

  @Override
  public Workspace createWorkspace(UUID accountId, WorkspaceRequest request) {

    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    if (isDuplicateNameEachAccount(request.getName(), accountId)) {
      throw new BadRequestException("Workspace name is existed");
    }

    Workspace workspace = Workspace.builder().account(account).name(request.getName()).build();

    return workspaceRepository.save(workspace);
  }

  @Override
  public Workspace updateWorkspace(UUID accountId, Integer workspaceId, WorkspaceRequest request) {

    Workspace workspace =
        workspaceRepository.getByIdAndAccount_Id(workspaceId, accountId).orElse(null);

    if (workspace == null) {
      throw new BadRequestException("Workspace doesn't exist");
    }

    if (workspace.getName().equals(AppConstant.DEFAULT_WORKSPACE_NAME)) {
      throw new BadRequestException("Can't update default workspace name");
    }

    if (isDuplicateNameEachAccount(request.getName(), accountId, workspaceId)) {
      throw new BadRequestException("Workspace name is existed");
    }

    workspace.setName(request.getName());
    return workspaceRepository.save(workspace);
  }

  @Override
  public void deleteWorkspace(UUID accountId, Integer workspaceId) {

    Workspace workspace =
        workspaceRepository.getByIdAndAccount_Id(workspaceId, accountId).orElse(null);

    if (workspace == null) {
      throw new BadRequestException("Workspace doesn't exist");
    }

    if (workspace.getName().equals(AppConstant.DEFAULT_WORKSPACE_NAME)) {
      throw new BadRequestException("Can't delete default workspace");
    }

    Set<RawTemplate> templates = workspace.getRawTemplates();
    if (templates.size() > 0) {
      Workspace defaultWorkspace =
          workspaceRepository
              .getByNameAndAccount_Id(AppConstant.DEFAULT_WORKSPACE_NAME, accountId)
              .orElseThrow(() -> new BadRequestException("Default workspace doesn't exist"));

      templates.forEach(t -> t.setWorkspace(defaultWorkspace));
      rawTemplateRepository.saveAll(templates);
      workspace.setRawTemplates(null);
    }

    workspaceRepository.delete(workspace);
  }

  private boolean isDuplicateNameEachAccount(String name, UUID accountId) {
    return workspaceRepository.getByNameAndAccount_Id(name, accountId).isPresent();
  }

  private boolean isDuplicateNameEachAccount(String name, UUID accountId, Integer workspaceId) {
    return workspaceRepository
        .getByNameAndAccount_IdAndIdNot(name, accountId, workspaceId)
        .isPresent();
  }
}
