package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.RatingTemplateRequest;
import fpt.capstone.etbs.payload.RatingUpdateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RatingRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.RatingService;
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
    public int templateRating(RatingTemplateRequest request) {
        int voting = 0;
        if (accountRepository.findById(request.getAccountId()).isPresent()
                && templateRepository.findById(request.getTemplateId()).isPresent()) {
            Rating rating = new Rating();
            rating.setAccount(accountRepository.findById(request.getAccountId()).get());
            rating.setActive(true);
            boolean status = request.isVote();
            rating.setVote(status);
            Template template = templateRepository.findById(request.getTemplateId()).get();
            voting = template.getVote();
            if (status) {
                voting += 1;
            } else {
                voting -= 1;
            }
            template.setVote(voting);
            rating.setTemplate(template);
            ratingRepository.save(rating);
            templateRepository.save(template);
        }
        return voting;
    }

    @Override
    public int templateRatingUpdate(RatingUpdateRequest request) {
        int vote = 0;
        if (ratingRepository.findById(request.getId()).isPresent()) {
            Rating rating = ratingRepository.findById(request.getId()).get();
            Template template = rating.getTemplate();
            if (rating.isActive() != request.isActive()) {
                rating.setActive(request.isActive());
            } else {
                rating.setVote(request.isVote());
                rating.setActive(request.isActive());
                if (!request.isVote()) {
                    template.setVote(template.getVote() - 2);
                } else {
                    template.setVote(template.getVote() + 2);
                }

            }
            ratingRepository.save(rating);

        }
        return vote;
    }
}
