package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.RawTemplateVersion;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.RawTemplateVersionRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.repository.WorkspaceRepository;
import fpt.capstone.etbs.service.RawTemplateService;
import fpt.capstone.etbs.service.RawTemplateVersionService;
import fpt.capstone.etbs.service.TemplateService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RawTemplateServiceImpl implements RawTemplateService {

  @Autowired
  private RawTemplateRepository rawTemplateRepository;

  @Autowired
  private WorkspaceRepository workspaceRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TemplateService templateService;

  @Autowired
  private RawTemplateVersionService rawTemplateVersionService;

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

    RawTemplate rawTemplate = RawTemplate.builder()
        .name(request.getName())
        .thumbnail(AppConstant.DEFAULT_RAW_TEMPLATE_THUMBNAIL)
        .description(request.getDescription())
        .workspace(workspace)
        .active(true)
        .build();
    rawTemplate.setVersions(
        Stream.of(RawTemplateVersion.builder()
            .name(AppConstant.DEFAULT_VERSION_NAME)
            .jsonContent("Default")
            .template(rawTemplate)
            .build())
            .collect(Collectors.toSet()));
    if (request.getTemplateId() != null) {
      Template template = templateService.getActiveTemplate(request.getTemplateId());
      if (template == null) {
        throw new BadRequestException("Template doesn't exist");
      }
      rawTemplate.setContent(template.getContent());
    }

    return rawTemplateRepository.save(rawTemplate);
  }

  private boolean isDuplicateNameEachWorkspace(String name, Integer workspaceId) {
    return rawTemplateRepository.getByNameAndWorkspace_Id(name, workspaceId).isPresent();
  }

}
