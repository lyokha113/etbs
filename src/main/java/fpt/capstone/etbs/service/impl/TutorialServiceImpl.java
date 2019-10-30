package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Tutorial;
import fpt.capstone.etbs.repository.TutorialRepository;
import fpt.capstone.etbs.service.TutorialService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TutorialServiceImpl implements TutorialService {

  @Autowired
  private TutorialRepository tutorialRepository;

  @Override
  public List<Tutorial> getTutorials() {
    List<Tutorial> tutorials = tutorialRepository.findAll();
    tutorials.forEach(t -> t.setContent(""));
    return tutorials;
  }

  @Override
  public List<Tutorial> getActiveTutorials() {
    List<Tutorial> tutorials = tutorialRepository.getByActiveTrue();
    tutorials.forEach(t -> t.setContent(""));
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
  public Tutorial createTutorial(Tutorial tutorial) {
    tutorial.setActive(true);
    return tutorialRepository.save(tutorial);
  }

  @Override
  public Tutorial updateTutorial(Integer id, Tutorial tutorial) {
    Tutorial current = tutorialRepository.findById(id).orElse(null);
    if (current == null) {
      throw new BadRequestException("Tutorial doesn't exist");
    }

    current.setName(tutorial.getName());
    current.setDescription(tutorial.getDescription());
    current.setContent(tutorial.getContent());
    current.setThumbnail(tutorial.getThumbnail());
    current.setActive(tutorial.isActive());
    return tutorialRepository.save(current);
  }
}
