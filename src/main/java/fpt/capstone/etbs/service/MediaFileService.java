package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.MediaFile;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MediaFileService {

  List<MediaFile> getMediaFilesOfAccount(UUID accountId);

  List<MediaFile> getMediaFilesOfAdministrator();

  List<MediaFile> getInactiveMediaFiles();

  List<MediaFile> createMediaFiles(UUID accountId, MultipartFile[] files) throws Exception;

  void deactivateMediaFile(UUID accountId, UUID id);

  void deleteMediaFile(List<MediaFile> files);
}
