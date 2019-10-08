package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.payload.MediaFileCreateRequest;
import fpt.capstone.etbs.payload.MediaFileStatusRequest;
import fpt.capstone.etbs.payload.MediaFileUpdateRequest;
import fpt.capstone.etbs.repository.MediaFileRepository;
import fpt.capstone.etbs.repository.MediaStorageRepository;
import fpt.capstone.etbs.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MediaFileServiceImpl implements MediaFileService {
    @Autowired
    MediaFileRepository mediaFileRepository;
    @Autowired
    MediaStorageRepository mediaStorageRepository;

    @Override
    public MediaFile createMediaFile(MediaFileCreateRequest request) {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setActive(true);
        mediaFile.setLink(request.getLink());
        mediaFile.setName(request.getName());
        mediaFile.setType(request.getType());
        if (mediaStorageRepository.findById(request.getStorageId()).isPresent()) {
            mediaFile.setMediaStorage(mediaStorageRepository.findById(request.getStorageId()).get());
        }
        mediaFileRepository.save(mediaFile);
        return mediaFile;
    }

    @Override
    public MediaFile getMediaFile(int id) {
        return mediaFileRepository.findById(id).orElse(null);
    }

    @Override
    public MediaFile updateMediaFile(MediaFileUpdateRequest request) {
        MediaFile mediaFile = getMediaFile(request.getId());
        if (mediaFile != null) {
            mediaFile.setName(request.getName());
            mediaFile.setType(request.getType());
            mediaFile.setLink(request.getLink());
        }
        return mediaFile;
    }

    @Override
    public List<MediaFile> getListMediaFile(UUID id) {
        return mediaFileRepository.getAllByMediaStorage_Account_Id(id);
    }

    @Override
    public MediaFile changeStatusMediaFile(MediaFileStatusRequest request) {
        MediaFile mediaFile = getMediaFile(request.getId());
        if (mediaFile != null) {
            mediaFile.setActive(request.isActive());
        }
        return mediaFile;
    }
}
