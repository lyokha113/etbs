package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Tutorial;
import fpt.capstone.etbs.payload.TutorialRequest;
import java.util.List;

public interface TutorialService {

  List<Tutorial> getTutorials();

  List<Tutorial> getActiveTutorials();

  Tutorial getTutorial(Integer id);

  Tutorial getActiveTutorial(Integer id);

  Tutorial createTutorial(TutorialRequest tutorial) throws Exception;

  Tutorial updateTutorial(Integer id, TutorialRequest tutorial) throws Exception;
}
