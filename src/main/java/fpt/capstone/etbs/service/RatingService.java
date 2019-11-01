package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.payload.RatingRequest;
import java.util.UUID;

public interface RatingService {

  Rating rate(UUID accountId, RatingRequest request);
}
