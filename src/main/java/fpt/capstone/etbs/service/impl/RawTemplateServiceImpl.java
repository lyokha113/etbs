package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.DeletingMediaFile;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.RawTemplateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.DeletingMediaFileRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.WorkspaceRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.ImageGenerator;
import fpt.capstone.etbs.service.RawTemplateService;
import fpt.capstone.etbs.service.TemplateService;
import java.awt.image.BufferedImage;
import java.util.UUID;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RawTemplateServiceImpl implements RawTemplateService {

  @Autowired
  private RawTemplateRepository rawTemplateRepository;

  @Autowired
  private WorkspaceRepository workspaceRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private DeletingMediaFileRepository deletingMediaFileRepository;

  @Autowired
  private TemplateService templateService;

  @Autowired
  private FirebaseService firebaseService;

  @Autowired
  private ImageGenerator imageGenerator;

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

    RawTemplate rawTemplate = RawTemplate.builder()
        .name(request.getName())
        .description(request.getDescription())
        .thumbnail(AppConstant.DEFAULT_RAW_TEMPLATE_THUMBNAIL)
        .content(
            "<p style=\"text-align: center; font-size: 40px; font-weight: bold\">Blank content</p>")
        .workspace(workspace)
        .build();

    rawTemplate = rawTemplateRepository.save(rawTemplate);

    if (request.getTemplateId() != null) {
      Template template = templateService.getTemplate(request.getTemplateId());
      if (template != null) {
        updateContent(accountId, rawTemplate.getId(), template.getContent());
      }
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
  public RawTemplate updateContent(UUID accountId, Integer id, String content) throws Exception {

    RawTemplate rawTemplate = rawTemplateRepository
        .getByIdAndWorkspace_Account_Id(id, accountId)
        .orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Raw template doesn't exist");
    }

    BufferedImage file = imageGenerator.generateImageFromHtmlWithChrome(content);
    String thumbnail = firebaseService.createRawThumbnail(file, rawTemplate.getId().toString());

    rawTemplate.setThumbnail(thumbnail);
    rawTemplate.setContent(content);
    return rawTemplateRepository.save(rawTemplate);
  }

  @Override
  public void deleteRawTemplate(UUID accountId, Integer id) {

    RawTemplate rawTemplate = rawTemplateRepository.getByIdAndWorkspace_Account_Id(id, accountId)
        .orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    DeletingMediaFile thumbnail = DeletingMediaFile
        .builder().link(rawTemplate.getThumbnail() + rawTemplate.getId())
        .build();

    deletingMediaFileRepository.save(thumbnail);
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
