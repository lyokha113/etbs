package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.payload.*;
import fpt.capstone.etbs.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class MediaFileController {

    @Autowired
    MediaFileService mediaFileService;

    @PostMapping("/media")
    public ResponseEntity<ApiResponse> createMediaFile(@Valid @RequestBody MediaFileCreateRequest request) {
        MediaFile mediaFile = mediaFileService.createMediaFile(request);
        return mediaFile != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "File created", mediaFile)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "File can not created!!", null));
    }

    @PutMapping("/media/{id}")
    public ResponseEntity<ApiResponse> updateMediaFile(
            @PathVariable("id") int id,
            @Valid @RequestBody MediaFileUpdateRequest request) {
        MediaFile mediaFile = mediaFileService.updateMediaFile(id, request);
        return mediaFile != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "The File updated", mediaFile)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "The File can not be updated!!", null));
    }

}
