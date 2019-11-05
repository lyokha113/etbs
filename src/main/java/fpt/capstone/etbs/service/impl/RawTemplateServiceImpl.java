package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.RawTemplateVersion;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateUpdateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.RawTemplateVersionRepository;
import fpt.capstone.etbs.repository.WorkspaceRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.RawTemplateService;
import fpt.capstone.etbs.service.TemplateService;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
  private TemplateService templateService;

  @Autowired
  private FirebaseService firebaseService;

  @Override
  public RawTemplate getRawTemplate(Integer id, UUID accountId) {
    return rawTemplateRepository.getByIdAndWorkspace_Account_Id(id, accountId).orElse(null);
  }

  @Override
  public RawTemplate createRawTemplate(UUID accountId, RawTemplateCreateRequest request)
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

    RawTemplate rawTemplate = RawTemplate.builder()
        .name(request.getName())
        .description(request.getDescription())
        .workspace(workspace)
        .build();

    RawTemplateVersion currentVersion = RawTemplateVersion.builder()
        .name(AppConstant.DEFAULT_VERSION_NAME)
        .content("Test Content")
        .thumbnail(AppConstant.DEFAULT_RAW_TEMPLATE_THUMBNAIL)
        .template(rawTemplate)
        .build();
    rawTemplate.setCurrentVersion(currentVersion);
    rawTemplate.setVersions(Stream.of(currentVersion).collect(Collectors.toSet()));
    return rawTemplateRepository.save(rawTemplate);
  }

  @Override
  public RawTemplate updateRawTemplate(UUID accountId, Integer id,
      RawTemplateUpdateRequest request) throws Exception {

    RawTemplate rawTemplate = rawTemplateRepository.getByIdAndWorkspace_Account_Id(id, accountId)
        .orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    if (isDuplicateNameEachWorkspace(request.getName(), rawTemplate.getWorkspace().getId(),
        rawTemplate.getId())) {
      throw new BadRequestException("Template name is existed in this workspace");
    }

    if (StringUtils.isEmpty(request.getName())) {
      rawTemplate.setName(request.getName());
    }

    if (StringUtils.isEmpty(request.getDescription())) {
      rawTemplate.setDescription(request.getDescription());
    }

    if (StringUtils.isEmpty(request.getContent()) && request.getThumbnail() != null) {
      rawTemplate.getCurrentVersion().setContent(request.getContent());

      String thumbnail = firebaseService
          .createTemplateThumbnail(request.getThumbnail(), rawTemplate.getId().toString());
      rawTemplate.getCurrentVersion().setThumbnail(thumbnail);
    }

    return rawTemplateRepository.save(rawTemplate);
  }

  @Override
  public RawTemplate updateRawTemplate(Integer templateId, RawTemplate rawTemplate,
      MultipartFile thumbnail) throws Exception {

    Template template = templateService.getActiveTemplate(templateId);
    if (template == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    RawTemplateUpdateRequest request = RawTemplateUpdateRequest.builder()
        .content(template.getContent())
        .thumbnail(thumbnail)
        .build();
    return updateRawTemplate(rawTemplate.getWorkspace().getAccount().getId(), rawTemplate.getId(),
        request);

  }

  @Override
  public RawTemplate changeVersion(UUID accountId, Integer id, Integer versionId) {

    RawTemplate rawTemplate = rawTemplateRepository
        .getByIdAndWorkspace_Account_Id(id, accountId).orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    RawTemplateVersion version = rawTemplateVersionRepository
        .getByIdAndTemplate_IdAndTemplate_Workspace_Account_Id(versionId, rawTemplate.getId(),
            accountId)
        .orElse(null);

    if (version == null) {
      throw new BadRequestException("Version doesn't exist");
    }

    rawTemplate.setCurrentVersion(version);
    return rawTemplateRepository.save(rawTemplate);
  }

  @Override
  public void deleteRawTemplate(UUID accountId, Integer id) {

    RawTemplate rawTemplate = rawTemplateRepository.getByIdAndWorkspace_Account_Id(id, accountId)
        .orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Template doesn't exist");
    }

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
