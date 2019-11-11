package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.RawTemplateVersion;
import fpt.capstone.etbs.payload.RawTemplateVersionRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.RawTemplateVersionRepository;
import fpt.capstone.etbs.service.RawTemplateVersionService;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RawTemplateVersionServiceImpl implements RawTemplateVersionService {

  private static final int MAX_VERSION_EACH_TEMPLATE = 5;

  @Autowired
  private RawTemplateRepository rawTemplateRepository;

  @Autowired
  private RawTemplateVersionRepository rawTemplateVersionRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Override
  public RawTemplateVersion createVersion(UUID accountId, RawTemplateVersionRequest request) {

    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    RawTemplate rawTemplate = rawTemplateRepository
        .getByIdAndWorkspace_Account_Id(request.getRawTemplateId(), accountId)
        .orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    if (rawTemplate.getVersions().size() == MAX_VERSION_EACH_TEMPLATE) {
      throw new BadRequestException("Maximum version");
    }

    if (isDuplicateNameEachTemplate(request.getName(), rawTemplate)) {
      throw new BadRequestException("Version name is existed in this template");
    }

    RawTemplateVersion version = RawTemplateVersion.builder()
        .name(request.getName())
        .template(rawTemplate)
        .content(rawTemplate.getCurrentVersion().getContent())
        .thumbnail(rawTemplate.getCurrentVersion().getThumbnail())
        .build();

    version = rawTemplateVersionRepository.save(version);
    rawTemplate.setCurrentVersion(version);
    rawTemplate.setVersions(Stream.concat(
        rawTemplate.getVersions().stream(), Stream.of(version))
        .collect(Collectors.toSet()));
    rawTemplateRepository.save(rawTemplate);

    return version;
  }

  @Override
  public RawTemplateVersion updateVersion(UUID accountId, Integer id,
      RawTemplateVersionRequest request) {

    RawTemplateVersion version = rawTemplateVersionRepository
        .getByIdAndTemplate_IdAndTemplate_Workspace_Account_Id(id, request.getRawTemplateId(),
            accountId)
        .orElse(null);

    if (version == null) {
      throw new BadRequestException("Version doesn't exist");
    }

    if (version.getName().equals(AppConstant.DEFAULT_VERSION_NAME)) {
      throw new BadRequestException("Can't update default version name");
    }

    if (isDuplicateNameEachTemplate(request.getName(), request.getRawTemplateId(), id)) {
      throw new BadRequestException("Version name is existed in this template");
    }

    version.setName(request.getName());
    return rawTemplateVersionRepository.save(version);
  }

  @Override
  public void deleteVersion(UUID accountId, Integer id) {

    RawTemplateVersion version = rawTemplateVersionRepository
        .getByIdAndTemplate_Workspace_Account_Id(id, accountId)
        .orElse(null);

    if (version == null) {
      throw new BadRequestException("Version doesn't exist");
    }

    if (version.getName().equals(AppConstant.DEFAULT_VERSION_NAME)) {
      throw new BadRequestException("Can't delete default version");
    }

    RawTemplate rawTemplate = version.getTemplate();
    if (version.getId().equals(rawTemplate.getCurrentVersion().getId())) {
      RawTemplateVersion defaultVersion = rawTemplate.getVersions().stream()
          .filter(v -> v.getName().equals(AppConstant.DEFAULT_VERSION_NAME)).findAny().orElse(null);
      rawTemplate.setCurrentVersion(defaultVersion);
      rawTemplateRepository.save(rawTemplate);
    }

    rawTemplateVersionRepository.delete(version);
  }

  private boolean isDuplicateNameEachTemplate(String name, RawTemplate rawTemplate) {
    return rawTemplate.getVersions().stream().anyMatch(v -> v.getName().equals(name));
  }

  private boolean isDuplicateNameEachTemplate(String name, Integer templateId, Integer id) {
    return rawTemplateVersionRepository.getByNameAndTemplate_IdAndIdNot(name, templateId, id)
        .isPresent();
  }
}
