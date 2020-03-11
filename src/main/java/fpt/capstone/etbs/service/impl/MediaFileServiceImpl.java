package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.DesignSession;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.MediaFileRepository;
import fpt.capstone.etbs.service.DesignSessionService;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.MediaFileService;
import fpt.capstone.etbs.util.StringUtils;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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
    return mediaFileRepository.getByAccount_IdIn(ids);
  }

  @Override
  public List<MediaFile> getInactiveMediaFiles() {
    return mediaFileRepository.getByActiveFalse();
  }

  @Override
  public List<MediaFile> getOwnerSessionMediaFiles(UUID ownerId) {
    return mediaFileRepository.getByAccount_IdAndActiveTrueAndOpenTrue(ownerId);
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
          .id(id).name(file.getOriginalFilename()).link(link).account(account).active(true).open(true)
          .build());
    }

    results = mediaFileRepository.saveAll(results);
    return  results;
  }

  @Override
  public List<MediaFile> createMediaFiles(UUID accountId, List<URL> files) throws Exception {
    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }
    List<MediaFile> results = new LinkedList<>();
    for (URL file : files) {
      UUID id = UUID.randomUUID();
      String link = firebaseService.createUserImage(file, accountId.toString(), id.toString());
      String name = StringUtils.generateRandomString(10);
      results.add(MediaFile.builder()
          .id(id).name(name).link(link).account(account).active(true).open(true)
          .build());
    }

    return mediaFileRepository.saveAll(results);
  }

  @Override
  public void deleteMediaFile(List<MediaFile> files) {
    mediaFileRepository.deleteAll(files);
  }

  @Override
  public void changeActiveMediaFile(UUID accountId, UUID id, boolean active, boolean isAdmin) {
    MediaFile mediaFile = isAdmin ?
        mediaFileRepository.findById(id).orElse(null) :
        mediaFileRepository.getByIdAndAccount_Id(id, accountId).orElse(null);
    if (mediaFile == null) {
      throw new BadRequestException("File doesn't exist");
    }

    mediaFile.setActive(active);
    mediaFileRepository.save(mediaFile);
  }

  @Override
  public void changeSharedMediaFile(UUID accountId, UUID id, boolean open) {
    MediaFile mediaFile =  mediaFileRepository.getByIdAndAccount_Id(id, accountId).orElse(null);

    if (mediaFile == null) {
      throw new BadRequestException("File doesn't exist");
    }

    mediaFile.setOpen(open);
    mediaFileRepository.save(mediaFile);
  }
}
