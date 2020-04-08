package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.DesignSession;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.DesignSessionRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface DesignSessionService {

  List<DesignSession> getSessions(UUID ownerId);

  List<DesignSession> getSessionsOfRaw(UUID ownerId, Integer rawId);

  List<DesignSession> getSessionsForUser(UUID contributorId);

  DesignSession getSessionForUser(UUID contributorId, Integer rawId);

  DesignSession createDesignSession(UUID ownerId, DesignSessionRequest request);

  RawTemplate updateContent(UUID contributorId, Integer rawId, String content) throws Exception;

  List<MediaFile> uploadFileToOwner(UUID contributorId, Integer rawId, MultipartFile[] files)
      throws Exception;

  void leaveSession(UUID contributorId, Integer rawId) throws Exception;

  void deleteSession(UUID ownerId, Integer rawId);

  void deleteSession(UUID ownerId, UUID contributorId, Integer rawId);

}
