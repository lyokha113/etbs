package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.payload.MediaFileCreateRequest;
import fpt.capstone.etbs.payload.MediaFileStatusRequest;
import fpt.capstone.etbs.payload.MediaFileUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MediaFileService {
    MediaFile createMediaFile(MediaFileCreateRequest request);
    MediaFile getMediaFile(int id);
    MediaFile updateMediaFile(MediaFileUpdateRequest request);
    List<MediaFile> getListMediaFile(UUID id);
    MediaFile changeStatusMediaFile(MediaFileStatusRequest request);
}
