package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.MediaStorage;
import fpt.capstone.etbs.payload.MediaStorageCreateRequest;
import fpt.capstone.etbs.payload.MediaStorageStatusRequest;
import fpt.capstone.etbs.payload.MediaStorageUpdateRequest;
import fpt.capstone.etbs.repository.MediaStorageRepository;
import fpt.capstone.etbs.service.MediaStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MediaStorageServiceImpl implements MediaStorageService {
    @Autowired
    MediaStorageRepository mediaStorageRepository;

    @Override
    public MediaStorage createMediaStorage(MediaStorageCreateRequest mediaStorageCreateRequest) {
        if (getMediaStorage(mediaStorageCreateRequest.getId()) == null) {
            MediaStorage mediaStorage = new MediaStorage();
            mediaStorage.setId(mediaStorageCreateRequest.getId());
            mediaStorage.setActive(true);
            mediaStorage.setLink(mediaStorageCreateRequest.getLink());
            mediaStorage.setName(mediaStorageCreateRequest.getName());
            mediaStorageRepository.save(mediaStorage);
            return mediaStorage;
        }
        return null;
    }

    @Override
    public MediaStorage getMediaStorage(UUID id) {
        return mediaStorageRepository.findById(id).orElse(null);
    }

    @Override
    public MediaStorage updateMediaStorage(MediaStorageUpdateRequest request) {
        MediaStorage mediaStorage = getMediaStorage(request.getId());
        if (mediaStorage != null) {
            mediaStorage.setName(request.getName());
            mediaStorage.setLink(request.getLink());
            mediaStorageRepository.save(mediaStorage);
        }
        return mediaStorage;
    }

    @Override
    public MediaStorage changeStatusMediaStorage(MediaStorageStatusRequest request) {
        MediaStorage mediaStorage = getMediaStorage(request.getId());
        if (mediaStorage != null) {
            mediaStorage.setActive(request.isActive());
            mediaStorageRepository.save(mediaStorage);
        }
        return mediaStorage;
    }
}
