package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.component.UserPrincipal;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.RatingRequest;
import fpt.capstone.etbs.payload.RatingResponse;
import fpt.capstone.etbs.service.RatingService;
import fpt.capstone.etbs.service.TemplateService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatingController {

  @Autowired
  private RatingService ratingService;

  @Autowired
  private TemplateService templateService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @PostMapping("/rating")
  private ResponseEntity<?> rate(
      @Valid @RequestBody RatingRequest request) {
    Authentication auth = authenticationFacade.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    Rating rate = ratingService.rate(userPrincipal.getId(), request);
    if (rate == null) {
      Template template = templateService.getTemplate(request.getTemplateId());
      rate = Rating.builder().template(template).build();
    }
    return ResponseEntity.ok(new ApiResponse<>(
        true, "Rate successful", RatingResponse.setResponse(rate)));
  }
}
