package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.payload.MediaFileUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface MediaFileService {

  List<MediaFile> getMediaFilesOfAccount(UUID accountId);

  MediaFile getMediaFileOfAccount(UUID id, UUID accountId);

  List<MediaFile> getInactiveMediaFiles();

  MediaFile createMediaFile(UUID accountId, UUID id, String name, String link);

  MediaFile updateMediaFile(UUID accountId, UUID id, MediaFileUpdateRequest request);

  void deactivateMediaFile(UUID accountId, UUID id);

  void deleteMediaFile(List<MediaFile> files);
}
