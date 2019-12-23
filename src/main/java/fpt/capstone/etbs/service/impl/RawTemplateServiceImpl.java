package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.RawTemplateVersion;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.RawTemplateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.RawTemplateVersionRepository;
import fpt.capstone.etbs.repository.WorkspaceRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.RawTemplateService;
import fpt.capstone.etbs.service.RawTemplateVersionService;
import fpt.capstone.etbs.service.TemplateService;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RawTemplateServiceImpl implements RawTemplateService {

  @Autowired
  private RawTemplateRepository rawTemplateRepository;

  @Autowired
  private RawTemplateVersionRepository rawTemplateVersionRepository;

  @Autowired
  private WorkspaceRepository workspaceRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private FirebaseService firebaseService;

  @Autowired
  private TemplateService templateService;

  @Autowired
  private RawTemplateVersionService rawTemplateVersionService;

  @Override
  public RawTemplate getRawTemplate(Integer id, UUID accountId) {
    return rawTemplateRepository.getByIdAndWorkspace_Account_Id(id, accountId).orElse(null);
  }

  @Override
  public RawTemplate createRawTemplate(UUID accountId, RawTemplateRequest request)
      throws Exception {
    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    Workspace workspace =
        workspaceRepository.getByIdAndAccount_Id(request.getWorkspaceId(), accountId).orElse(null);

    if (workspace == null) {
      throw new BadRequestException("Workspace doesn't exist");
    }

    if (isDuplicateNameEachWorkspace(request.getName(), workspace)) {
      throw new BadRequestException("Template name is existed in this workspace");
    }

    Template template = null;
    if (request.getTemplateId() != null) {
      template = templateService.getActiveTemplate(request.getTemplateId());
      if (template == null) {
        throw new BadRequestException("Template doesn't exist");
      }
    }

    RawTemplate rawTemplate = RawTemplate.builder()
        .name(request.getName())
        .description(request.getDescription())
        .workspace(workspace)
        .build();

    RawTemplateVersion currentVersion = RawTemplateVersion.builder()
        .name(AppConstant.DEFAULT_VERSION_NAME)
        .content(
            "<p style=\"text-align: center; font-size: 40px; font-weight: bold\">Blank content</p>")
        .thumbnail(AppConstant.DEFAULT_RAW_TEMPLATE_THUMBNAIL)
        .rawTemplate(rawTemplate)
        .build();

    rawTemplate.setCurrentVersion(currentVersion);
    rawTemplate.setVersions(Stream.of(currentVersion).collect(Collectors.toSet()));
    rawTemplate = rawTemplateRepository.save(rawTemplate);

    if (template != null) {
      RawTemplateVersion version = rawTemplateVersionService
          .updateContent(accountId, rawTemplate.getId(), template.getContent());
      rawTemplate.setCurrentVersion(version);
    }

    return rawTemplate;
  }


  @Override
  public RawTemplate updateRawTemplate(UUID accountId, Integer id, RawTemplateRequest request) {

    RawTemplate rawTemplate = rawTemplateRepository.getByIdAndWorkspace_Account_Id(id, accountId)
        .orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    if (isDuplicateNameEachWorkspace(request.getName(), rawTemplate.getWorkspace().getId(),
        rawTemplate.getId())) {
      throw new BadRequestException("Template name is existed in this workspace");
    }

    if (!StringUtils.isEmpty(request.getName())) {
      rawTemplate.setName(request.getName());
    }

    if (!StringUtils.isEmpty(request.getDescription())) {
      rawTemplate.setDescription(request.getDescription());
    }

    if (request.getWorkspaceId() != null) {
      Workspace workspace = workspaceRepository
          .getByIdAndAccount_Id(request.getWorkspaceId(), accountId).orElse(null);

      if (workspace == null) {
        throw new BadRequestException("Workspace doesn't exist");
      }

      rawTemplate.setWorkspace(workspace);
    }

    return rawTemplateRepository.save(rawTemplate);
  }

  @Override
  public RawTemplate changeVersion(UUID accountId, Integer id, Integer versionId) {

    RawTemplate rawTemplate = rawTemplateRepository
        .getByIdAndWorkspace_Account_Id(id, accountId).orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    RawTemplateVersion version = rawTemplateVersionRepository
        .getByIdAndRawTemplate_Workspace_Account_Id(versionId, accountId)
        .orElse(null);

    if (version == null) {
      throw new BadRequestException("Version doesn't exist");
    }

    rawTemplate.setCurrentVersion(version);
    return rawTemplateRepository.save(rawTemplate);
  }

  @Override
  public void deleteRawTemplate(UUID accountId, Integer id) throws Exception {

    RawTemplate rawTemplate = rawTemplateRepository.getByIdAndWorkspace_Account_Id(id, accountId)
        .orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    firebaseService.deleteImage(AppConstant.RAW_TEMPLATE_THUMBNAIL + id);
    rawTemplateRepository.delete(rawTemplate);
  }

  private boolean isDuplicateNameEachWorkspace(String name, Workspace workspace) {
    return workspace.getRawTemplates().stream().anyMatch(rt -> rt.getName().equals(name));
  }

  private boolean isDuplicateNameEachWorkspace(String name, Integer workspaceId, Integer id) {
    return rawTemplateRepository.getByNameAndWorkspace_IdAndIdNot(name, workspaceId, id)
        .isPresent();
  }

}
