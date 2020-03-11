package fpt.capstone.etbs.service.impl;

import static fpt.capstone.etbs.constant.AppConstant.CURRENT_ONLINE_CACHE;
import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_RAW_QUEUE;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.DeletingMediaFile;
import fpt.capstone.etbs.model.DesignSession;
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
import fpt.capstone.etbs.service.DesignSessionService;
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
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
        .content(AppConstant.BLANK_CONTENT)
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
  public RawTemplate updateContent(UUID accountId, Integer id, String content) {
    RawTemplate rawTemplate = rawTemplateRepository
        .getByIdAndWorkspace_Account_Id(id, accountId)
        .orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Raw template doesn't exist");
    }

    rawTemplate.setContent(content);
    rawTemplate = rawTemplateRepository.save(rawTemplate);
    return rawTemplate;
  }

  @Override
  public void uploadFiles(UUID accountId, Integer rawId, MultipartFile[] files) throws Exception {

    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    mediaFileService.createMediaFiles(account.getId(), files);

    String currentOnlineKey = CURRENT_ONLINE_CACHE + rawId;
    String dest = WEB_SOCKET_RAW_QUEUE + rawId;
    List<MediaFileResponse> ownerFiles = mediaFileService.getOwnerSessionMediaFiles(account.getId())
        .stream().map(MediaFileResponse::setResponse)
        .collect(Collectors.toList());

    Set<Object> currentOnline = redisService.getOnlineSessions(currentOnlineKey);
    currentOnline.forEach(user -> {
      String receiverId = String.valueOf(user);
      messagePublisherService.sendDesignContent(receiverId, dest, ownerFiles);
    });
  }

  @Override
  @Async("generateImageAsyncExecutor")
  public void updateThumbnail(RawTemplate rawTemplate) throws Exception {
    BufferedImage file = imageGeneratorService.generateImageFromHtml(rawTemplate.getContent());
    String thumbnail = firebaseService.createRawThumbnail(file, rawTemplate.getId().toString());
    rawTemplate.setThumbnail(thumbnail);
    rawTemplateRepository.save(rawTemplate);
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
