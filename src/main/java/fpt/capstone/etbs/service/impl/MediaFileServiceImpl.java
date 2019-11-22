package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.MediaFileRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.MediaFileService;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaFileServiceImpl implements MediaFileService {

  @Autowired
  private MediaFileRepository mediaFileRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private FirebaseService firebaseService;

  @Override
  public List<MediaFile> getMediaFilesOfAccount(UUID accountId) {
    return mediaFileRepository.getByAccount_Id(accountId);
  }

  @Override
  public List<MediaFile> getMediaFilesOfAdministrator() {
    List<Account> accounts = accountRepository.getByRole_Id(RoleEnum.ADMINISTRATOR.getId());
    List<UUID> ids = accounts.stream().map(Account::getId).collect(Collectors.toList());
    return mediaFileRepository.getByAccount_Id(ids);
  }

  @Override
  public List<MediaFile> getInactiveMediaFiles() {
    return mediaFileRepository.getByActiveFalse();
  }

  @Override
  public List<MediaFile> createMediaFiles(UUID accountId, MultipartFile[] files) throws Exception {
    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }
    List<MediaFile> results = new LinkedList<>();
    for (MultipartFile file : files) {
      UUID id = UUID.randomUUID();
      String link = firebaseService.createUserImage(file, accountId.toString(), id.toString());
      results.add(MediaFile.builder()
          .id(id).name(file.getOriginalFilename()).link(link).account(account).active(true)
          .build());
    }

    return mediaFileRepository.saveAll(results);
  }

  @Override
  public void deleteMediaFile(List<MediaFile> files) {
    mediaFileRepository.deleteAll(files);
  }

  @Override
  public void deactivateMediaFile(UUID accountId, UUID id) {
    MediaFile mediaFile = mediaFileRepository.getByIdAndAccount_Id(accountId, id).orElse(null);
    if (mediaFile == null) {
      throw new BadRequestException("File doesn't exist");
    }

    mediaFile.setActive(false);
    mediaFileRepository.save(mediaFile);
  }
}
