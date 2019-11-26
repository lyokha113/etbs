package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Tutorial;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.TutorialRequest;
import fpt.capstone.etbs.payload.TutorialResponse;
import fpt.capstone.etbs.service.TutorialService;
import fpt.capstone.etbs.util.RoleUtils;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TutorialController {

  @Autowired
  private TutorialService tutorialService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @GetMapping("/tutorial")
  public ResponseEntity<ApiResponse> getTutorials() {
    Authentication auth = authenticationFacade.getAuthentication();
    List<Tutorial> tutorials = RoleUtils.hasAdminRole(auth)
        ? tutorialService.getTutorials()
        : tutorialService.getActiveTutorials();
    List<TutorialResponse> response =
        tutorials.stream().map(TutorialResponse::setResponse).collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @GetMapping("/tutorial/{id}")
  public ResponseEntity<ApiResponse> getTutorial(@PathVariable("id") Integer id) {
    Authentication auth = authenticationFacade.getAuthentication();
    Tutorial response = RoleUtils.hasAdminRole(auth)
        ? tutorialService.getTutorial(id)
        : tutorialService.getActiveTutorial(id);
    return response != null
        ? ResponseEntity.ok(
        new ApiResponse<>(true, "", TutorialResponse.setResponseWithContent(response)))
        : ResponseEntity.badRequest().body(new ApiResponse<>(true, "Not found", null));
  }

  @PostMapping("/tutorial")
  public ResponseEntity<ApiResponse> createTutorial(@Valid @ModelAttribute TutorialRequest tutorial)
      throws Exception {
    try {
      Tutorial response = tutorialService.createTutorial(tutorial);
      response = tutorialService.updateThumbnail(response, tutorial.getThumbnail());
      return ResponseEntity.ok(
          new ApiResponse<>(true, "Tutorial created", TutorialResponse.setResponse(response)));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/tutorial/{id}")
  public ResponseEntity<ApiResponse> updateTutorial(
      @PathVariable("id") Integer id, @Valid @ModelAttribute TutorialRequest tutorial)
      throws Exception {
    try {
      Tutorial response = tutorialService.updateTutorial(id, tutorial);
      return ResponseEntity.ok(
          new ApiResponse<>(true, "Tutorial updated", TutorialResponse.setResponse(response)));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PatchMapping("/tutorial/{id}")
  public ResponseEntity<ApiResponse> updateStatusTutorial(
      @PathVariable("id") Integer id,
      @RequestParam("active") boolean active)
      throws Exception {
    try {
      Tutorial response = tutorialService.updateStatusTutorial(id, active);
      return ResponseEntity.ok(
          new ApiResponse<>(true, "Tutorial status updated",
              TutorialResponse.setResponse(response)));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
