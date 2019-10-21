package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.payload.MediaFileUpdateRequest;
import fpt.capstone.etbs.repository.MediaFileRepository;
import fpt.capstone.etbs.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Override
    public MediaFile getMediaFile(int id) {
        return mediaFileRepository.findById(id).orElse(null);
    }


    @Override
    public List<MediaFile> getListMediaFile(UUID id) {
        return null;
    }

    @Override
    public void createMediaFile(MediaFile mediaFile) {
        mediaFileRepository.save(mediaFile);
    }

    @Override
    public MediaFile updateMediaFile(int id, MediaFileUpdateRequest request) {
        MediaFile mediaFile = getMediaFile(id);
        if (mediaFile != null) {
            mediaFile.setName(request.getName());
            mediaFile.setLink(request.getLink());
            mediaFile.setActive(request.isActive());
        }
        return mediaFile;
    }

}
