package fpt.capstone.etbs.service;

import fpt.capstone.etbs.constant.DesignSessionStatus;
import fpt.capstone.etbs.model.DesignSession;
import fpt.capstone.etbs.payload.DesignSessionRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface DesignSessionService {

  List<DesignSession> getSessionsOfRaw(UUID ownerId, Integer rawId);

  List<DesignSession> getSessionsForUser(UUID contributorId);

  DesignSession getSessionForUser(UUID contributorId, Integer rawId);

  DesignSession createDesignSession(UUID ownerId, DesignSessionRequest request);

  DesignSession changeStatus(UUID contributorId, Integer rawId, Boolean status);

  void updateContent(UUID contributorId, Integer rawId, String content) throws Exception;

  void uploadFileToOwner(UUID contributorId, Integer rawId, MultipartFile[] files) throws Exception;

  void closeJoin(UUID ownerId, UUID contributorId, Integer rawId);

  void closeJoin(UUID ownerId, Integer rawId);

}
