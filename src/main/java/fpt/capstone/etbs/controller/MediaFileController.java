package fpt.capstone.etbs.controller;

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
import java.util.UUID;

@RestController
public class MediaFileController {

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/media")
    public ResponseEntity<ApiResponse> createMediaFile(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("filename") String filename,
                                                       Authentication auth) throws Exception {

        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        UUID uuid = UUID.randomUUID();
        String url = firebaseService.createImage("/" + userPrincipal.getId(), file, uuid.toString());
        MediaFile mediaFile = MediaFile
                .builder()
                .id(uuid)
                .name(filename)
                .link(url)
                .account(accountService.getAccount(userPrincipal.getId()))
                .build();
        mediaFileService.createMediaFile(mediaFile);

        return mediaFile != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "File created", new MediaFileResponse(mediaFile))) :
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
