package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Tutorial;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.WorkspaceRequest;
import fpt.capstone.etbs.payload.WorkspaceResponse;
import fpt.capstone.etbs.service.TutorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TutorialController {

    @Autowired
    private TutorialService tutorialService;

    @GetMapping("/tutorials")
    public ResponseEntity<ApiResponse> getTutorials() {
        List<Tutorial> response = tutorialService.getTutorials();
        return ResponseEntity.ok(new ApiResponse<>(true, "", response));
    }

    @GetMapping("/tutorials/{id}")
    public ResponseEntity<ApiResponse> getTutorial(@PathVariable("id") Integer id) {
        Tutorial response = tutorialService.getTutorial(id);
        return response != null ?
                ResponseEntity.ok(new ApiResponse<>(true, "", response)) :
                ResponseEntity.badRequest().body(new ApiResponse<>(true, "Not found", null));
    }

    @GetMapping("/tutorial")
    public ResponseEntity<ApiResponse> getActiveTutorials() {
        List<Tutorial> response = tutorialService.getActiveTutorials();
        return ResponseEntity.ok(new ApiResponse<>(true, "", response));
    }

    @GetMapping("/tutorial/{id}")
    public ResponseEntity<ApiResponse> getActiveTutorial(@PathVariable("id") Integer id) {
        Tutorial response = tutorialService.getActiveTutorial(id);
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
