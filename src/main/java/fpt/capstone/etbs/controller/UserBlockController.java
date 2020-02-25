package fpt.capstone.etbs.controller;


import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.constant.UserEmailStatus;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.UserBlock;
import fpt.capstone.etbs.model.UserEmail;
import fpt.capstone.etbs.payload.AccountResponse;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.StringWrapperRequest;
import fpt.capstone.etbs.payload.UserBlockRequest;
import fpt.capstone.etbs.payload.UserBlockResponse;
import fpt.capstone.etbs.payload.UserEmailResponse;
import fpt.capstone.etbs.service.UserBlockService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserBlockController {


  @Autowired
  private UserBlockService userBlockService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @GetMapping("/userblock")
  public ResponseEntity<?> getUserBlock() {
    try {
      Authentication auth = authenticationFacade.getAuthentication();
      UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
      List<UserBlock> blocks = userBlockService.getUserBlocks(userPrincipal.getId());
      List<UserBlockResponse> responses = blocks.stream().map(UserBlockResponse::setResponse)
          .collect(Collectors.toList());
      return ResponseEntity.ok(new ApiResponse<>(true, "", responses));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PostMapping("/userblock")
  private ResponseEntity<?> createUserBlock(@Valid @RequestBody UserBlockRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      UserBlock block = userBlockService.createUserBlock(userPrincipal.getId(), request);
      UserBlockResponse response = UserBlockResponse.setResponse(block);
      return ResponseEntity.ok(new ApiResponse<>(true, "Block was created", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));

    }
  }

  @PutMapping("/userblock/{id}")
  private ResponseEntity<?> updateUserBlock(
      @PathVariable("id") int id,
      @Valid @RequestBody UserBlockRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      UserBlock block = userBlockService.updateUserBlock(userPrincipal.getId(), id, request);
      UserBlockResponse response = UserBlockResponse.setResponse(block);
      return ResponseEntity.ok(new ApiResponse<>(true, "Block was updated", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));

    }
  }

  @PatchMapping("/userblock/{id}")
  private ResponseEntity<?> updateUserBlockContent(
      @PathVariable("id") int id,
      @Valid @RequestBody StringWrapperRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      UserBlock block = userBlockService.updateUserBlockContent(userPrincipal.getId(), id, request.getString());
      UserBlockResponse response = UserBlockResponse.setResponse(block);
      return ResponseEntity.ok(new ApiResponse<>(true, "Block content was updated", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));

    }
  }

  @DeleteMapping("/userblock/{id}")
  public ResponseEntity<?> deleteUserBlock(@PathVariable("id") int id) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    try {
      userBlockService.deleteUserBlock(userPrincipal.getId(), id);
      return ResponseEntity.ok(new ApiResponse<>(true, "Block was deleted", null));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
