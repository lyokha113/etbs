package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.MediaStorage;
import fpt.capstone.etbs.payload.MediaStorageCreateRequest;
import fpt.capstone.etbs.payload.MediaStorageStatusRequest;
import fpt.capstone.etbs.payload.MediaStorageUpdateRequest;

import java.util.UUID;

public interface MediaStorageService {
    MediaStorage createMediaStorage(MediaStorageCreateRequest request);
    MediaStorage getMediaStorage(UUID id);
    MediaStorage updateMediaStorage(MediaStorageUpdateRequest request);
    MediaStorage changeStatusMediaStorage(MediaStorageStatusRequest request);
}
