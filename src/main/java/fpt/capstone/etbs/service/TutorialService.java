package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Tutorial;

import java.util.List;

public interface TutorialService {

  List<Tutorial> getTutorials();

  List<Tutorial> getActiveTutorials();

  Tutorial getTutorial(Integer id);

  Tutorial getActiveTutorial(Integer id);

  Tutorial createTutorial(Tutorial tutorial);

  Tutorial updateTutorial(Integer id, Tutorial tutorial);
}
