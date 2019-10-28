package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.MediaFileResponse;
import fpt.capstone.etbs.payload.MediaFileUpdateRequest;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class MediaFileController {

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private FirebaseService firebaseService;

    @GetMapping("/file")
    public ResponseEntity<ApiResponse> getMediaFileOfAccount(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        List<MediaFile> files = mediaFileService.getMediaFilesOfAccount(userPrincipal.getId());
        List<MediaFileResponse> response = files.stream()
                .map(MediaFileResponse::setResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "", response));
    }

    @PostMapping("/file")
    public ResponseEntity<ApiResponse> createMediaFile(
            Authentication auth,
            @RequestParam("file") MultipartFile file,
            @RequestParam("filename") String filename) throws Exception {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        UUID id = UUID.randomUUID();
        String link = firebaseService.createImage(userPrincipal.getId().toString(), file, id.toString());
        try {
            MediaFile mediaFile = mediaFileService.createMediaFile(userPrincipal.getId(), id, filename, link);
            MediaFileResponse response = MediaFileResponse.setResponse(mediaFile);
            return ResponseEntity.ok(new ApiResponse<>(true, "File created", response));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }

    @PutMapping("/file/{id}")
    public ResponseEntity<ApiResponse> updateMediaFile(
            Authentication auth,
            @PathVariable("id") UUID id,
            @Valid @RequestBody MediaFileUpdateRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        try {
            MediaFile mediaFile = mediaFileService.updateMediaFile(userPrincipal.getId(), id, request);
            MediaFileResponse response = MediaFileResponse.setResponse(mediaFile);
            return ResponseEntity.ok(new ApiResponse<>(true, "File updated", response));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }

    @DeleteMapping("/file/{id}")
    public ResponseEntity<ApiResponse> deleteMediaFile(
            Authentication auth,
            @PathVariable("id") UUID id) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        try {
            mediaFileService.deactivateMediaFile(userPrincipal.getId(), id);
            return ResponseEntity.ok(new ApiResponse<>(true, "File deleted", null));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
        }

    }

}
