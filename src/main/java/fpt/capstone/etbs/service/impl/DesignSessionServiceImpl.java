package fpt.capstone.etbs.service.impl;

import static fpt.capstone.etbs.constant.AppConstant.CURRENT_ONLINE_CACHE;
import static fpt.capstone.etbs.constant.AppConstant.MAX_DESIGN_SESSION;
import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_RAW_QUEUE;

import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.DesignSession;
import fpt.capstone.etbs.model.DesignSessionIdentity;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.DesignSessionRequest;
import fpt.capstone.etbs.payload.DesignSessionResponse;
import fpt.capstone.etbs.payload.MediaFileResponse;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.DesignSessionRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.service.DesignSessionService;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.ImageGeneratorService;
import fpt.capstone.etbs.service.MediaFileService;
import fpt.capstone.etbs.service.MessagePublisherService;
import fpt.capstone.etbs.service.RedisService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DesignSessionServiceImpl implements DesignSessionService {

  @Autowired
  private DesignSessionRepository designSessionRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private RawTemplateRepository rawTemplateRepository;

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
  public List<DesignSession> getSessions(UUID ownerId) {
    return designSessionRepository.getByRawTemplate_Workspace_Account_Id(ownerId);
  }

  @Override
  public List<DesignSession> getSessionsOfRaw(UUID ownerId, Integer rawId) {
    return designSessionRepository
        .getByRawTemplate_Workspace_Account_IdAndId_RawTemplateId(ownerId, rawId);
  }

  @Override
  public List<DesignSession> getSessionsForUser(UUID contributorId) {
    return designSessionRepository.getById_ContributorId(contributorId);
  }

  @Override
  public DesignSession getSessionForUser(UUID contributorId, Integer rawId) {
    return designSessionRepository
        .getById_ContributorIdAndId_RawTemplateId(contributorId, rawId)
        .orElse(null);
  }

  @Override
  public DesignSession createDesignSession(UUID ownerId, DesignSessionRequest request) {

    Account owner = accountRepository.findById(ownerId).orElse(null);
    if (owner == null) {
      throw new BadRequestException("Owner doesn't exist");
    }

    if (owner.getEmail().equals(request.getContributorEmail())) {
      throw new BadRequestException("Please don't invite yourself.");
    }

    Account contributor = accountRepository.getByEmail(request.getContributorEmail()).orElse(null);
    if (contributor == null) {
      throw new BadRequestException("Email doesn't exist");
    }

    if (contributor.getRole().getName().equals(RoleEnum.ADMINISTRATOR.getName()) ||
        !contributor.isAllowInvite()) {
      throw new BadRequestException("User isn't allowed to invite");
    }

    RawTemplate raw = rawTemplateRepository.findById(request.getRawId()).orElse(null);
    if (raw == null) {
      throw new BadRequestException("Raw template doesn't exist");
    }

    List<DesignSession> sessions = designSessionRepository
        .getByRawTemplate_Workspace_Account_IdAndId_RawTemplateId(ownerId, request.getRawId());
    if (sessions.size() >= MAX_DESIGN_SESSION) {
      throw new BadRequestException("We currently support maximum " + MAX_DESIGN_SESSION
          + " contributor at the same time");
    }

    DesignSession session = designSessionRepository
        .getByContributor_EmailAndId_RawTemplateId(request.getContributorEmail(),
            request.getRawId())
        .orElse(null);

    if (session != null) {
      throw new BadRequestException("This user has been invited.");
    }

    DesignSessionIdentity identity = DesignSessionIdentity.builder()
        .contributorId(contributor.getId()).rawTemplateId(request.getRawId())
        .build();
    session = DesignSession.builder()
        .id(identity).contributor(contributor).rawTemplate(raw)
        .build();
    session = designSessionRepository.save(session);

    messagePublisherService.sendAddInvitation(contributor.getId().toString(),
        DesignSessionResponse.setResponse(session));
    return session;
  }

  @Override
  public RawTemplate updateContent(UUID contributorId, Integer rawId, String content) {
    DesignSession session = designSessionRepository
        .getById_ContributorIdAndId_RawTemplateId(contributorId, rawId)
        .orElse(null);

    if (session == null) {
      throw new BadRequestException("Session doesn't exist");
    }

    RawTemplate rawTemplate = session.getRawTemplate();
    rawTemplate.setContent(content);
    return rawTemplateRepository.save(rawTemplate);
  }

  @Override
  public List<MediaFile>  uploadFileToOwner(UUID contributorId, Integer rawId, MultipartFile[] files)
      throws Exception {

    DesignSession session = designSessionRepository
        .getById_ContributorIdAndId_RawTemplateId(contributorId, rawId)
        .orElse(null);

    if (session == null) {
      throw new BadRequestException("Session doesn't exist");
    }

    Account owner = session.getRawTemplate().getWorkspace().getAccount();
    List<MediaFile> uploaded = mediaFileService.createMediaFiles(owner.getId(), files);

    String currentOnlineKey = CURRENT_ONLINE_CACHE + rawId;
    String dest = WEB_SOCKET_RAW_QUEUE + rawId;
    List<MediaFileResponse> ownerFiles = mediaFileService.getOwnerSessionMediaFiles(owner.getId())
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
  public void leaveSession(UUID contributorId, Integer rawId) {
    DesignSession session = designSessionRepository
        .getById_ContributorIdAndId_RawTemplateId(contributorId, rawId)
        .orElse(null);

    if (session == null) {
      throw new BadRequestException("Session doesn't exist");
    }

    Account owner = session.getRawTemplate().getWorkspace().getAccount();
    designSessionRepository.delete(session);

    redisService.setOfflineSession(CURRENT_ONLINE_CACHE + rawId, contributorId.toString());
    messagePublisherService.sendLeaveSession(owner.getId().toString(),
        WEB_SOCKET_RAW_QUEUE + rawId,
        contributorId.toString());

  }

  @Override
  public void deleteSession(UUID ownerId, Integer rawId) {
    List<DesignSession> sessions = designSessionRepository
        .getByRawTemplate_Workspace_Account_IdAndId_RawTemplateId(ownerId, rawId);

    List<String> contributors = sessions.stream()
        .map(s -> s.getContributor().getId().toString())
        .collect(Collectors.toList());

    designSessionRepository.deleteAll(sessions);
    contributors.forEach(contributor -> {
      messagePublisherService.sendRemoveInvitation(contributor, rawId);
      messagePublisherService.sendKickSession(contributor, WEB_SOCKET_RAW_QUEUE + rawId);
      redisService.setOfflineSession(CURRENT_ONLINE_CACHE + rawId, contributor);
    });
  }

  @Override
  public void deleteSession(UUID ownerId, UUID contributorId, Integer rawId) {
    DesignSession session = designSessionRepository
        .getById_ContributorIdAndId_RawTemplateId(contributorId, rawId)
        .orElse(null);

    if (session == null) {
      throw new BadRequestException("Session doesn't exist");
    }

    if (!session.getRawTemplate().getWorkspace().getAccount().getId().equals(ownerId)) {
      throw new BadRequestException("Permission denied");
    }

    designSessionRepository.delete(session);
    messagePublisherService.sendRemoveInvitation(contributorId.toString(), rawId);
    messagePublisherService.sendKickSession(contributorId.toString(), WEB_SOCKET_RAW_QUEUE + rawId);
    redisService.setOfflineSession(CURRENT_ONLINE_CACHE + rawId, contributorId.toString());
  }

}
