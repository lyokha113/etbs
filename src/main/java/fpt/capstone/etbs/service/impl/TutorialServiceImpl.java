package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Tutorial;
import fpt.capstone.etbs.payload.TutorialRequest;
import fpt.capstone.etbs.repository.TutorialRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.TutorialService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TutorialServiceImpl implements TutorialService {

  @Autowired
  private TutorialRepository tutorialRepository;

  @Autowired
  private FirebaseService firebaseService;

  @Override
  public List<Tutorial> getTutorials() {
    List<Tutorial> tutorials = tutorialRepository.findAll();
    tutorials.forEach(t -> t.setContent(null));
    return tutorials;
  }

  @Override
  public List<Tutorial> getActiveTutorials() {
    List<Tutorial> tutorials = tutorialRepository.getByActiveTrue();
    tutorials.forEach(t -> t.setContent(null));
    return tutorials;
  }

  @Override
  public Tutorial getTutorial(Integer id) {
    return tutorialRepository.findById(id).orElse(null);
  }

  @Override
  public Tutorial getActiveTutorial(Integer id) {
    return tutorialRepository.getByIdAndActiveTrue(id).orElse(null);
  }

  @Override
  public Tutorial createTutorial(TutorialRequest request) throws Exception {
    Tutorial tutorial = Tutorial.builder()
        .name(request.getName())
        .content(request.getContent())
        .description(request.getDescription())
        .thumbnail(AppConstant.DEFAULT_TUTORIAL_THUMBNAIL)
        .active(true)
        .build();

    tutorial = tutorialRepository.save(tutorial);

    String thumbnail = firebaseService
        .createTutorialThumbnail(request.getThumbnail(), tutorial.getId().toString());

    tutorial.setThumbnail(thumbnail);
    return tutorialRepository.save(tutorial);
  }

  @Override
  public Tutorial updateTutorial(Integer id, TutorialRequest request) throws Exception {

    Tutorial tutorial = tutorialRepository.findById(id).orElse(null);
    if (tutorial == null) {
      throw new BadRequestException("Tutorial doesn't exist");
    }

    String thumbnail = firebaseService
        .createTutorialThumbnail(request.getThumbnail(), tutorial.getId().toString());

    tutorial.setName(request.getName());
    tutorial.setDescription(request.getDescription());
    tutorial.setContent(request.getContent());
    tutorial.setActive(request.isActive());
    tutorial.setThumbnail(thumbnail);

    return tutorialRepository.save(tutorial);
  }
}
