package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.MediaStorage;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.MediaStorageCreateRequest;
import fpt.capstone.etbs.payload.MediaStorageStatusRequest;
import fpt.capstone.etbs.payload.MediaStorageUpdateRequest;
import fpt.capstone.etbs.service.MediaStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/storage")
public class MediaStorageController {
    @Autowired
    MediaStorageService mediaStorageService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createMediaStorage(@Valid @RequestBody MediaStorageCreateRequest request) {
        MediaStorage mediaStorage = mediaStorageService.createMediaStorage(request);
        return mediaStorage != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Storage created", mediaStorage)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Storage can not created!!", null));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateMediaStorage(@Valid @RequestBody MediaStorageUpdateRequest request) {
        MediaStorage mediaStorage = mediaStorageService.updateMediaStorage(request);
        return mediaStorage !=null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "The storage updated", mediaStorage)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "The storage can not be updated!!", null));
    }

    @PutMapping("/status")
    public ResponseEntity<ApiResponse> changeStatusMediaStorage(@Valid @RequestBody MediaStorageStatusRequest request){
        MediaStorage mediaStorage = mediaStorageService.changeStatusMediaStorage(request);
        return mediaStorage !=null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "The storage updated", mediaStorage)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "The storage can not be updated!!", null));
    }
}
