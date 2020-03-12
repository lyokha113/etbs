package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.Tutorial;
import fpt.capstone.etbs.payload.TutorialRequest;
import fpt.capstone.etbs.repository.TutorialRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.MediaFileService;
import fpt.capstone.etbs.service.TutorialService;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TutorialServiceImpl implements TutorialService {

  @Autowired
  private TutorialRepository tutorialRepository;

  @Autowired
  private MediaFileService mediaFileService;

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
  public Tutorial createTutorial(UUID accountId, TutorialRequest request) throws Exception {

    Tutorial tutorial = Tutorial.builder()
        .name(request.getName())
        .content(request.getContent())
        .description(request.getDescription())
        .thumbnail(AppConstant.DEFAULT_TUTORIAL_THUMBNAIL)
        .active(true)
        .build();

    tutorial = tutorialRepository.save(tutorial);
    tutorial = updateTutorialContentImage(accountId, tutorial);
    return tutorial;
  }

  @Override
  public Tutorial updateTutorial(UUID accountId, Integer id, TutorialRequest request) throws Exception {

    Tutorial tutorial = tutorialRepository.findById(id).orElse(null);
    if (tutorial == null) {
      throw new BadRequestException("Tutorial doesn't exist");
    }

    tutorial.setName(request.getName());
    tutorial.setDescription(request.getDescription());
    tutorial.setContent(request.getContent());

    if (request.getThumbnail() != null) {
      String thumbnail = firebaseService
          .createTutorialThumbnail(request.getThumbnail(), tutorial.getId().toString());
      tutorial.setThumbnail(thumbnail);
    }

    tutorial = tutorialRepository.save(tutorial);
    tutorial = updateTutorialContentImage(accountId, tutorial);
    return tutorial;
  }

  @Override
  public Tutorial updateStatusTutorial(Integer id, boolean status) throws Exception {
    Tutorial tutorial = tutorialRepository.findById(id).orElse(null);
    if (tutorial == null) {
      throw new BadRequestException("Tutorial doesn't exist");
    }
    tutorial.setActive(status);
    return tutorialRepository.save(tutorial);
  }

  @Override
  public Tutorial updateThumbnail(Tutorial tutorial, MultipartFile thumbnail) throws Exception {
    String link = firebaseService
        .createTutorialThumbnail(thumbnail, tutorial.getId().toString());
    tutorial.setThumbnail(link);
    return tutorialRepository.save(tutorial);
  }

  private Tutorial updateTutorialContentImage(UUID accountId, Tutorial tutorial)
      throws Exception {
    String content = tutorial.getContent();
    Document doc = Jsoup.parse(content, "UTF-8");
    List<URL> externalLinks = new ArrayList<>();
    for (Element element : doc.select("img")) {
      String src = element.absUrl("src");
      if (!src.startsWith("http://storage.googleapis.com/") && !src.contains(AppConstant.USER_IMAGES)) {
        externalLinks.add(new URL(src));
      }
    }

    if (!externalLinks.isEmpty()) {
      List<MediaFile> files = mediaFileService.createMediaFiles(accountId, externalLinks);
      for (int i = 0; i < files.size(); i++) {
        content = content.replace(externalLinks.get(i).toString(), files.get(i).getLink());
      }

      tutorial.setContent(content);
      return tutorialRepository.save(tutorial);
    }

    return tutorial;

  }
}
