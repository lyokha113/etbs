package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.payload.MediaFileCreateRequest;
import fpt.capstone.etbs.payload.MediaFileUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MediaFileService {
    MediaFile createMediaFile(MediaFileCreateRequest request);
    MediaFile getMediaFile(int id);
    MediaFile updateMediaFile(int id, MediaFileUpdateRequest request);
    List<MediaFile> getListMediaFile(UUID id);
}
