package fpt.capstone.etbs.service.impl;

import static fpt.capstone.etbs.constant.AppConstant.MAX_DESIGN_SESSION;
import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_INVITATION_QUEUE;
import static fpt.capstone.etbs.constant.AppConstant.WEB_SOCKET_RAW_QUEUE;

import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.DesignSession;
import fpt.capstone.etbs.model.DesignSessionIdentity;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.DesignSessionRequest;
import fpt.capstone.etbs.payload.DesignSessionResponse;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.DesignSessionRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.service.DesignSessionService;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.ImageGeneratorService;
import fpt.capstone.etbs.service.MediaFileService;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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
  private SimpMessageSendingOperations messagingTemplate;

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

    Map<String, Object> message = new HashMap<>();
    message.put("data", DesignSessionResponse.setResponse(session));
    message.put("command", "add");
    messagingTemplate
        .convertAndSendToUser(contributor.getId().toString(), WEB_SOCKET_INVITATION_QUEUE, message);
    return session;
  }

  @Override
  public void updateContent(UUID contributorId, Integer rawId, String content) throws Exception {
    DesignSession session = designSessionRepository
        .getById_ContributorIdAndId_RawTemplateId(contributorId, rawId)
        .orElse(null);

    if (session == null) {
      throw new BadRequestException("Session doesn't exist");
    }

    RawTemplate rawTemplate = session.getRawTemplate();

    BufferedImage file = imageGeneratorService.generateImageFromHtml(content);
    String thumbnail = firebaseService.createRawThumbnail(file, rawTemplate.getId().toString());
    rawTemplate.setThumbnail(thumbnail);
    rawTemplate.setContent(content);
    rawTemplateRepository.save(rawTemplate);
  }

  @Override
  public void uploadFileToOwner(UUID contributorId, Integer rawId, MultipartFile[] files)
      throws Exception {

    DesignSession session = designSessionRepository
        .getById_ContributorIdAndId_RawTemplateId(contributorId, rawId)
        .orElse(null);

    if (session == null) {
      throw new BadRequestException("Session doesn't exist");
    }

    Account owner = session.getRawTemplate().getWorkspace().getAccount();
    mediaFileService.createMediaFiles(owner.getId(), files, true);
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

    Map<String, Object> message = new HashMap<>();
    message.put("data", contributorId.toString());
    message.put("command", "leave");
    messagingTemplate.convertAndSendToUser(
        owner.getId().toString(), WEB_SOCKET_RAW_QUEUE + rawId, message);
  }

  @Override
  public void deleteSession(UUID ownerId, Integer rawId) {
    List<DesignSession> sessions = designSessionRepository
        .getByRawTemplate_Workspace_Account_IdAndId_RawTemplateId(ownerId, rawId);

    List<String> contributors = sessions.stream()
        .map(s -> s.getContributor().getId().toString())
        .collect(Collectors.toList());

    designSessionRepository.deleteAll(sessions);

    Map<String, Object> message = new HashMap<>();
    message.put("command", "remove");
    message.put("data", rawId);
    contributors.forEach(
        c -> messagingTemplate.convertAndSendToUser(c, WEB_SOCKET_INVITATION_QUEUE, message));
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
    Map<String, Object> message = new HashMap<>();
    message.put("command", "remove");
    message.put("data", rawId);
    messagingTemplate
        .convertAndSendToUser(contributorId.toString(), WEB_SOCKET_INVITATION_QUEUE, message);
  }

}
