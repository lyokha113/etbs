package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Tutorial;
import fpt.capstone.etbs.payload.TutorialRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface TutorialService {

  List<Tutorial> getTutorials();

  List<Tutorial> getActiveTutorials();

  Tutorial getTutorial(Integer id);

  Tutorial getActiveTutorial(Integer id);

  Tutorial createTutorial(UUID accountId, TutorialRequest tutorial) throws Exception;

  Tutorial updateTutorial(UUID accountId, Integer id, TutorialRequest tutorial) throws Exception;

  Tutorial updateStatusTutorial(Integer id, boolean status) throws Exception;

  Tutorial updateThumbnail(Tutorial tutorial, MultipartFile thumbnail) throws Exception;
}
