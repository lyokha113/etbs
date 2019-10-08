package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.payload.*;
import fpt.capstone.etbs.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/Media")
public class MediaFileController {
    @Autowired
    MediaFileService mediaFileService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createMediaFile(@Valid @RequestBody MediaFileCreateRequest request) {
        MediaFile mediaFile = mediaFileService.createMediaFile(request);
        return mediaFile != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "File created", mediaFile)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "File can not created!!", null));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateMediaFile(@Valid @RequestBody MediaFileUpdateRequest request) {
        MediaFile mediaFile = mediaFileService.updateMediaFile(request);
        return mediaFile != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "The File updated", mediaFile)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "The File can not be updated!!", null));
    }

    @PutMapping("/status")
    public ResponseEntity<ApiResponse> changeStatusMediaFile(@Valid @RequestBody MediaFileStatusRequest request) {
        MediaFile mediaFile = mediaFileService.changeStatusMediaFile(request);
        return mediaFile != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "The media file updated", mediaFile)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "The media file can not be updated!!", null));
    }
}
