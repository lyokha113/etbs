package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.RatingTemplateRequest;
import fpt.capstone.etbs.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class RatingController {
    @Autowired
    RatingService ratingService;

    @PostMapping("/rating")
    private ResponseEntity<ApiResponse> createRatingTemplate(@Valid @RequestBody RatingTemplateRequest request) {
        int vote = ratingService.templateRating(request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Template rating successful", vote));
    }
}
