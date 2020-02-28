package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.DesignSessionStatus;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.DesignSession;
import fpt.capstone.etbs.model.DesignSessionIdentity;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.DesignSessionRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.DesignSessionRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.service.DesignSessionService;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.ImageGeneratorService;
import fpt.capstone.etbs.service.MediaFileService;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;
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

  @Override
  public List<DesignSession> getSessionsOfRaw(UUID ownerId, Integer rawId) {
    return designSessionRepository.getByRawTemplate_Workspace_Account_IdAndId_RawId(ownerId, rawId);
  }

  @Override
  public List<DesignSession> getSessionsForUser(UUID contributorId) {
    return designSessionRepository.getById_ContributorId(contributorId);
  }

  @Override
  public DesignSession getSessionForUser(UUID contributorId, Integer rawId) {
    return designSessionRepository
        .getById_ContributorIdAndId_RawIdAndStatus(contributorId, rawId, DesignSessionStatus.JOINED)
        .orElse(null);
  }

  @Override
  public DesignSession createDesignSession(UUID ownerId, DesignSessionRequest request) {

    Account owner = accountRepository.findById(ownerId).orElse(null);
    if (owner == null) {
      throw new BadRequestException("Owner doesn't exist");
    }

    List<DesignSession> currentSessions = designSessionRepository
        .getByRawTemplate_Workspace_Account_IdAndId_RawId(ownerId, request.getRawId());

    if (currentSessions.size() >= AppConstant.MAX_DESIGN_SESSION) {
      throw new BadRequestException("We currently support maximum " + AppConstant.MAX_DESIGN_SESSION
          + " user to edit at the same time");
    }

    Account contributor = accountRepository.getByEmail(request.getContributorEmail()).orElse(null);
    if (contributor == null) {
      throw new BadRequestException("Contributor doesn't exist");
    }

    DesignSessionIdentity identity = DesignSessionIdentity.builder()
        .contributorId(contributor.getId()).rawId(request.getRawId())
        .build();
    DesignSession session = DesignSession.builder()
        .id(identity).status(DesignSessionStatus.PENDING)
        .build();

    return designSessionRepository.save(session);
  }

  @Override
  public void updateContent(UUID contributorId, Integer rawId, String content)
      throws Exception {
    DesignSession session = designSessionRepository
        .getById_ContributorIdAndId_RawIdAndStatus(contributorId, rawId, DesignSessionStatus.JOINED)
        .orElse(null);

    if (session == null) {
      throw new BadRequestException("Session doesn't exist or was closed");
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
        .getById_ContributorIdAndId_RawIdAndStatus(contributorId, rawId, DesignSessionStatus.JOINED)
        .orElse(null);

    if (session == null) {
      throw new BadRequestException("Session doesn't exist or was closed");
    }

    Account owner = session.getRawTemplate().getWorkspace().getAccount();
    mediaFileService.createMediaFiles(owner.getId(), files);
  }

  @Override
  public DesignSession changeStatus(UUID contributorId, Integer rawId, Boolean status) {
    DesignSession session = designSessionRepository
        .getById_ContributorIdAndId_RawId(contributorId, rawId)
        .orElse(null);

    if (session == null || session.getStatus().equals(DesignSessionStatus.CLOSED)) {
      throw new BadRequestException("Session doesn't exist or was closed");
    }

    session.setStatus(status ? DesignSessionStatus.JOINED : DesignSessionStatus.REJECTED);
    return designSessionRepository.save(session);
  }

  @Override
  public void closeJoin(UUID ownerId, UUID contributorId, Integer rawId) {
    DesignSession session = designSessionRepository
        .getById_ContributorIdAndId_RawId(contributorId, rawId)
        .orElse(null);

    if (session == null) {
      throw new BadRequestException("Session doesn't exist");
    }

    if (!session.getRawTemplate().getWorkspace().getAccount().getId().equals(ownerId)) {
      throw new BadRequestException("Permission denied");
    }

    session.setStatus(DesignSessionStatus.CLOSED);
    designSessionRepository.save(session);
  }

  @Override
  public void closeJoin(UUID ownerId, Integer rawId) {
    List<DesignSession> sessions = designSessionRepository
        .getByRawTemplate_Workspace_Account_IdAndId_RawId(ownerId, rawId);

    sessions.forEach(s -> s.setStatus(DesignSessionStatus.CLOSED));
    designSessionRepository.saveAll(sessions);
  }

}
