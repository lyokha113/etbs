package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.UserPrincipal;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.RatingRequest;
import fpt.capstone.etbs.payload.RatingResponse;
import fpt.capstone.etbs.service.RatingService;
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

  @PostMapping("/rating")
  private ResponseEntity<ApiResponse> rate(
      Authentication auth, @Valid @RequestBody RatingRequest request) {
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    Rating rate = ratingService.rate(userPrincipal.getId(), request);
    return rate == null
        ? ResponseEntity.ok(new ApiResponse<>(
        true, "Rated successful", RatingResponse.setResponse(rate)))
        : ResponseEntity.ok(new ApiResponse<>(
            true, "Unrated successful", null));
  }
}
