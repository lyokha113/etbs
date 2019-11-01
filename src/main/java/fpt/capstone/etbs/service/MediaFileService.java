package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.payload.MediaFileUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MediaFileService {

  List<MediaFile> getMediaFilesOfAccount(UUID accountId);

  MediaFile getMediaFileOfAccount(UUID id, UUID accountId);

  List<MediaFile> getInactiveMediaFiles();

  MediaFile createMediaFile(UUID accountId, UUID id, String name, MultipartFile file)
      throws Exception;

  MediaFile updateMediaFile(UUID accountId, UUID id, MediaFileUpdateRequest request);

  void deactivateMediaFile(UUID accountId, UUID id);

  void deleteMediaFile(List<MediaFile> files);
}
