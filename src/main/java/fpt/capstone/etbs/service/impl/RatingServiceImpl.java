package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.RatingIdentity;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.RatingRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RatingRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.RatingService;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceImpl implements RatingService {

  @Autowired
  private RatingRepository ratingRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TemplateRepository templateRepository;

  @Override
  public Rating rate(UUID accountId, RatingRequest request) {

    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    Template template =
        templateRepository.getByIdAndActiveTrue(request.getTemplateId()).orElse(null);
    if (template == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    Rating rating =
        template.getRatings().stream()
            .filter(r -> r.getId().getAccountId().equals(accountId))
            .findFirst()
            .orElse(null);

    if (rating == null) {
      rating = Rating.builder()
          .id(RatingIdentity.builder().accountId(accountId).templateId(template.getId()).build())
          .account(account).template(template).vote(request.isVote()).build();

      rating = ratingRepository.save(rating);
      template.setRatings(Stream.of(rating).collect(Collectors.toSet()));
      return rating;
    }

    if (!rating.getAccount().getId().equals(accountId)) {
      throw new BadRequestException("Invalid permission rating");
    }

    if (rating.isVote() == request.isVote()) {
      Rating finalRating = rating;
      template.getRatings().removeIf(r -> r.equals(finalRating));
      templateRepository.save(template);
      return null;
    }

    rating.setVote(request.isVote());
    return ratingRepository.save(rating);
  }
}
