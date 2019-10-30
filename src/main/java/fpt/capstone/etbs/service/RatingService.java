package fpt.capstone.etbs.service;

import fpt.capstone.etbs.payload.RatingTemplateRequest;
import fpt.capstone.etbs.payload.RatingUpdateRequest;

public interface RatingService {
    int templateRating(RatingTemplateRequest request);
    int templateRatingUpdate(RatingUpdateRequest request);
}
