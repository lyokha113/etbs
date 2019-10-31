package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.RatingRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RatingRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.RatingService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceImpl implements RatingService {

  @Autowired
  RatingRepository ratingRepository;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  TemplateRepository templateRepository;

  @Override
  public Rating rate(UUID accountId, RatingRequest request) {

    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    Template template = templateRepository.getByIdAndActiveTrue(request.getTemplateId())
        .orElse(null);
    if (template == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    Rating rating = template.getRatings().stream()
        .filter(r -> r.getAccount().getId().equals(accountId))
        .findFirst()
        .orElse(null);

    if (rating == null) {
      rating = Rating.builder()
          .account(account)
          .template(template)
          .like(request.isLike())
          .build();
      return ratingRepository.save(rating);

    } else {

      if (!rating.getAccount().getId().equals(accountId)) {
        throw new BadRequestException("Invalid permission rating");
      }

      if (rating.isLike() == request.isLike()) {
        ratingRepository.delete(rating);
        return null;
      } else {
        rating.setLike(request.isLike());
        return ratingRepository.save(rating);
      }
    }
  }

}
