package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.WorkspaceRepository;
import fpt.capstone.etbs.service.RawTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RawTemplateServiceImpl implements RawTemplateService {
  @Autowired private RawTemplateRepository rawTemplateRepository;

  @Autowired private WorkspaceRepository workspaceRepository;

  @Autowired private AccountRepository accountRepository;

  @Override
  public RawTemplate getRawTemplate(Integer id, UUID accountId) {
    return rawTemplateRepository.getByIdAndWorkspace_Account_Id(id, accountId).orElse(null);
  }

  @Override
  public RawTemplate createRawTemplate(UUID accountId, RawTemplateCreateRequest request) {
    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    Workspace workspace =
        workspaceRepository.getByIdAndAccount_Id(request.getWorkspaceId(), accountId).orElse(null);
    if (workspace == null) {
      throw new BadRequestException("Workspace doesn't exist");
    }

    if (isDuplicateNameEachWorkspace(request.getName(), workspace.getId())) {
      throw new BadRequestException("Template name is existed in this workspace");
    }

    RawTemplate rawTemplate =
        RawTemplate.builder()
            .name(request.getName())
            .content(request.getContent())
            .thumbnail(request.getThumbnail())
            .description(request.getDescription())
            .workspace(workspace)
            .build();

    return rawTemplateRepository.save(rawTemplate);
  }

  private boolean isDuplicateNameEachWorkspace(String name, Integer workspaceId) {
    return rawTemplateRepository.getByNameAndWorkspace_Id(name, workspaceId).isPresent();
  }

  //    @Override
  //    public RawTemplate createTemplate(UUID accountId, RawTemplateCreateRequest request) {
  //
  //
  //
  //        RawTemplate rawTemplate = new RawTemplate();
  //        rawTemplate.setContent(request.getContent());
  //        rawTemplate.setDescription(request.getDescription());
  //        rawTemplate.setName(request.getName());
  //        Workspace workspace = workspaceRepository.findById(request.getWorkspaceId()).get();
  //        rawTemplate.setWorkspace(workspace);
  //        rawTemplate.setActive(true);
  //        rawTemplateRepository.save(rawTemplate);
  //
  //        RawTemplateCreateResponse response = new RawTemplateCreateResponse();
  //        response.setId(rawTemplate.getId());
  //        return response;
  //    }
  //
  //    @Override
  //    public List<RawTemplateResponse> getTemplateByWorkspaceId(int wId) {
  //        List<RawTemplate> rawTemplate = rawTemplateRepository.getAllByWorkspace_Id(wId);
  //        List<RawTemplateResponse> response = new ArrayList<>();
  //        for (int i = 0; i < rawTemplate.size(); i++) {
  //            RawTemplateResponse temp = new RawTemplateResponse();
  //            temp.setId(rawTemplate.get(i).getId());
  //            temp.setName(rawTemplate.get(i).getName());
  //            temp.setWorkspaceId(wId);
  //            temp.setContent(rawTemplate.get(i).getContent());
  //            temp.setDescription(rawTemplate.get(i).getDescription());
  //            temp.setThumbnail(rawTemplate.get(i).getThumbnail());
  //
  //            response.add(temp);
  //        }
  //
  //        return response;
  //    }

}
