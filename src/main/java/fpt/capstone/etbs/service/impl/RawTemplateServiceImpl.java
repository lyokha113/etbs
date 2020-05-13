package fpt.capstone.etbs.service.impl;

import static fpt.capstone.etbs.constant.AppConstant.CURRENT_ONLINE_CACHE;
import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_RAW_QUEUE;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.DeletingMediaFile;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.MediaFileResponse;
import fpt.capstone.etbs.payload.RawTemplateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.DeletingMediaFileRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.WorkspaceRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.ImageGeneratorService;
import fpt.capstone.etbs.service.MediaFileService;
import fpt.capstone.etbs.service.MessagePublisherService;
import fpt.capstone.etbs.service.RawTemplateService;
import fpt.capstone.etbs.service.RedisService;
import fpt.capstone.etbs.service.TemplateService;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
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
  private MediaFileService mediaFileService;

  @Autowired
  private FirebaseService firebaseService;

  @Autowired
  private ImageGeneratorService imageGeneratorService;

  @Autowired
  private MessagePublisherService messagePublisherService;

  @Autowired
  private RedisService redisService;

  @Override
  public RawTemplate getRawTemplate(Integer id, UUID accountId) {
    return rawTemplateRepository.getByIdAndWorkspace_Account_Id(id, accountId).orElse(null);
  }

  @Override
  public RawTemplate createRawTemplate(UUID accountId, RawTemplateRequest request) {
    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    Workspace workspace =
        workspaceRepository.getByIdAndAccount_Id(request.getWorkspaceId(), accountId).orElse(null);

    if (workspace == null) {
      throw new BadRequestException("Workspace doesn't exist");
    }

    if (isDuplicateTemplateName(request.getName(), accountId)) {
      throw new BadRequestException("Template name is existed");
    }

    RawTemplate rawTemplate = RawTemplate.builder()
        .name(request.getName())
        .description(request.getDescription())
        .thumbnail(AppConstant.DEFAULT_RAW_TEMPLATE_THUMBNAIL)
        .content(AppConstant.BLANK_CONTENT)
        .workspace(workspace)
        .build();

    rawTemplate = rawTemplateRepository.save(rawTemplate);

    if (request.getTemplateId() != null) {
      Template template = templateService.getTemplate(request.getTemplateId());
      if (template != null) {
        updateContent(accountId, rawTemplate.getId(), template.getContent());
      }
    } else if (request.getRawId() != null) {
      RawTemplate raw = getRawTemplate(request.getRawId(), accountId);
      if (raw != null) {
        updateContent(accountId, rawTemplate.getId(), raw.getContent());
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

    if (isDuplicateTemplateName(request.getName(), rawTemplate.getId(), accountId)) {
      throw new BadRequestException("Template name is existed");
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
  public RawTemplate updateContent(UUID accountId, Integer id, String content) {
    RawTemplate rawTemplate = rawTemplateRepository
        .getByIdAndWorkspace_Account_Id(id, accountId)
        .orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Raw template doesn't exist");
    }

    if (StringUtils.isEmpty(content)) {
      throw new BadRequestException("Content is empty");
    }

    rawTemplate.setContent(content);
    rawTemplate = rawTemplateRepository.save(rawTemplate);
    return rawTemplate;
  }

  @Override
  public RawTemplate updateContentContributor(UUID contributorId, Integer id, String content) {
    RawTemplate rawTemplate = rawTemplateRepository.findById(id).orElse(null);

    if (rawTemplate == null || rawTemplate.getDesignSessions().stream().noneMatch(s -> s.getContributor().getId().equals(contributorId))) {
      throw new BadRequestException("Raw template doesn't exist");
    }

    rawTemplate.setContent(content);
    rawTemplate = rawTemplateRepository.save(rawTemplate);
    return rawTemplate;
  }

  @Override
  public List<MediaFile> uploadFiles(UUID accountId, Integer rawId, MultipartFile[] files)
      throws Exception {

    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    List<MediaFile> uploaded = mediaFileService.createMediaFiles(account.getId(), files);

    String currentOnlineKey = CURRENT_ONLINE_CACHE + rawId;
    String dest = WEB_SOCKET_RAW_QUEUE + rawId;
    List<MediaFileResponse> ownerFiles = mediaFileService.getOwnerSessionMediaFiles(account.getId())
        .stream().map(MediaFileResponse::setResponse)
        .collect(Collectors.toList());

    Set<Object> currentOnline = redisService.getOnlineSessions(currentOnlineKey);
    currentOnline.forEach(user -> {
      String receiverId = String.valueOf(user);
      messagePublisherService.sendDesignFiles(receiverId, dest, ownerFiles);
    });

    return uploaded;
  }

  @Override
  @Async("generateImageAsyncExecutor")
  public void updateThumbnail(RawTemplate rawTemplate) {
    try {
      BufferedImage file = imageGeneratorService.generateImageFromHtml(rawTemplate.getContent());
      String thumbnail = firebaseService.createRawThumbnail(file, rawTemplate.getId().toString());
      rawTemplate.setThumbnail(thumbnail);
      rawTemplateRepository.save(rawTemplate);
    } catch (Exception ex) {
      log.error("Generate image from html failed", ex);
    }

  }

  @Override
  public void deleteRawTemplate(UUID accountId, Integer id) {

    RawTemplate rawTemplate = rawTemplateRepository.getByIdAndWorkspace_Account_Id(id, accountId)
        .orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    DeletingMediaFile thumbnail = DeletingMediaFile
        .builder().link(AppConstant.RAW_TEMPLATE_THUMBNAIL + rawTemplate.getId())
        .build();

    deletingMediaFileRepository.save(thumbnail);
    rawTemplateRepository.delete(rawTemplate);
  }

  private boolean isDuplicateTemplateName(String name, UUID accountId) {
    List<Workspace> workspaces = workspaceRepository.getByAccount_Id(accountId);
    for (Workspace workspace: workspaces) {
      if (workspace.getRawTemplates().stream().anyMatch(rt -> rt.getName().equals(name))) {
        return true;
      }
    }
    return false;
  }

  private boolean isDuplicateTemplateName(String name, Integer rawId, UUID accountId) {
    List<Workspace> workspaces = workspaceRepository.getByAccount_Id(accountId);
    for (Workspace workspace: workspaces) {
      if (workspace.getRawTemplates().stream()
          .anyMatch(rt -> rt.getName().equals(name) && !rt.getId().equals(rawId))) {
        return true;
      }
    }
    return false;
  }

}
