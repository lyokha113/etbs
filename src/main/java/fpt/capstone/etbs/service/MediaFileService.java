package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.MediaFile;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MediaFileService {

  List<MediaFile> getMediaFilesOfAccount(UUID accountId);

  List<MediaFile> getMediaFilesOfAdministrator();

  List<MediaFile> getInactiveMediaFiles();

  List<MediaFile> createMediaFiles(UUID accountId, MultipartFile[] files) throws Exception;

  List<MediaFile> createMediaFiles(UUID accountId, List<URL> files) throws Exception;

  void changeActiveMediaFile(UUID accountId, UUID id, boolean active, boolean isAdmin);

  void changeSharedMediaFile(UUID accountId, UUID id, boolean open);

  void deleteMediaFile(List<MediaFile> files);
}
