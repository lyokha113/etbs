package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.Tutorial;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.service.TutorialService;
import fpt.capstone.etbs.util.RoleUtils;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TutorialController {

  @Autowired
  private TutorialService tutorialService;

  @GetMapping("/tutorial")
  public ResponseEntity<ApiResponse> getActiveTutorials(Authentication auth) {
    List<Tutorial> response = RoleUtils.hasAdminRole(auth)
        ? tutorialService.getTutorials()
        : tutorialService.getActiveTutorials();
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @GetMapping("/tutorial/{id}")
  public ResponseEntity<ApiResponse> getActiveTutorial(
      Authentication auth,
      @PathVariable("id") Integer id) {
    Tutorial response = RoleUtils.hasAdminRole(auth)
        ? tutorialService.getTutorial(id)
        : tutorialService.getActiveTutorial(id);
    return response != null ?
        ResponseEntity.ok(new ApiResponse<>(true, "", response)) :
        ResponseEntity.badRequest().body(new ApiResponse<>(true, "Not found", null));
  }

  @PostMapping("/tutorial")
  public ResponseEntity<ApiResponse> createTutorial(@Valid @RequestBody Tutorial tutorial) {
    Tutorial response = tutorialService.createTutorial(tutorial);
    return response != null ?
        ResponseEntity.ok(new ApiResponse<>(true, "Tutorial created", response)) :
        ResponseEntity.badRequest().body(new ApiResponse<>(true, "Tutorial create failed", null));
  }

  @PutMapping("/tutorial/{id}")
  public ResponseEntity<ApiResponse> updateTutorial(
      @PathVariable("id") Integer id,
      @Valid @RequestBody Tutorial tutorial) {
    Tutorial response = tutorialService.updateTutorial(id, tutorial);
    return response != null ?
        ResponseEntity.ok(new ApiResponse<>(true, "Tutorial updated", response)) :
        ResponseEntity.badRequest().body(new ApiResponse<>(true, "Tutorial update failed", null));
  }
}
