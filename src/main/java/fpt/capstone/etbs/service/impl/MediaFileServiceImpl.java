package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.payload.MediaFileUpdateRequest;
import fpt.capstone.etbs.repository.MediaFileRepository;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private AccountService accountService;

    @Override
    public List<MediaFile> getMediaFilesOfAccount(UUID accountId) {
        return mediaFileRepository.getByAccount_Id(accountId);
    }

    @Override
    public List<MediaFile> getInactiveMediaFiles() {
        return mediaFileRepository.getByActiveFalse();
    }

    @Override
    public MediaFile createMediaFile(UUID accountId, UUID id, String name, String link) {

        Account account = accountService.getAccount(accountId);
        if (account == null) throw new BadRequestException("Account doesn't exist");

        MediaFile mediaFile = MediaFile
                .builder()
                .id(id)
                .name(name)
                .link(link)
                .account(account)
                .active(true)
                .build();

        return mediaFileRepository.save(mediaFile);
    }


    @Override
    public MediaFile updateMediaFile(UUID accountId, UUID id, MediaFileUpdateRequest request) {

        MediaFile mediaFile = mediaFileRepository.getByIdAndAccount_Id(accountId, id).orElse(null);
        if (mediaFile == null) throw new BadRequestException("File doesn't exist");

        mediaFile.setName(request.getName());
        mediaFile.setActive(request.isActive());
        return mediaFileRepository.save(mediaFile);
    }

    @Override
    public void deleteMediaFile(List<MediaFile> files) {
        mediaFileRepository.deleteAll(files);
    }

    @Override
    public MediaFile deactivateMediaFile(UUID accountId, UUID id) {
        MediaFile mediaFile = mediaFileRepository.getByIdAndAccount_Id(accountId, id).orElse(null);
        if (mediaFile == null) throw new BadRequestException("File doesn't exist");

        mediaFile.setActive(false);
        return mediaFileRepository.save(mediaFile);
    }


}
