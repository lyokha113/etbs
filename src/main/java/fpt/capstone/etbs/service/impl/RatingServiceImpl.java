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

    Template template = templateRepository.findById(request.getTemplateId()).orElse(null);
    if (template == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    Rating rating =
        template.getRatings().stream()
            .filter(r -> r.getId().getAccountId().equals(accountId))
            .findAny()
            .orElse(null);

    if (rating == null) {
      RatingIdentity identity = RatingIdentity.builder()
          .accountId(accountId).templateId(template.getId())
          .build();
      rating = Rating.builder()
          .id(identity).account(account).template(template).vote(request.isVote())
          .build();
      template.getRatings().add(rating);
    } else if (rating.isVote() != request.isVote()) {
      rating.setVote(request.isVote());
    } else if (rating.isVote() == request.isVote()) {
      template.getRatings().remove(rating);
      ratingRepository.delete(rating);
    }

    templateRepository.save(template);
    return rating;
  }
}
