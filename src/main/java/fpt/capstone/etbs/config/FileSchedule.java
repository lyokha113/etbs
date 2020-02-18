package fpt.capstone.etbs.config;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.model.DeletingMediaFile;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.repository.DeletingMediaFileRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.MediaFileService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableScheduling
public class FileSchedule {

  private static final int THREAD_POOL = 5;
  private final Logger logger = LoggerFactory.getLogger(FileSchedule.class);
  @Autowired
  private FirebaseService firebaseService;

  @Autowired
  private MediaFileService mediaFileService;

  @Autowired
  private DeletingMediaFileRepository deletingMediaFileRepository;

  @Bean
  public TaskScheduler taskScheduler() {
    final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(THREAD_POOL);
    return scheduler;
  }

  @Scheduled(initialDelay = 1000 * 60 * 5, fixedDelay = 1000 * 60 * 5)
  public void deleteInactiveUserFile() {
    logger.info("Start to delete inactive user file");
    List<MediaFile> files = mediaFileService.getInactiveMediaFiles();
    files = files.stream()
        .filter(f -> f.getLastModifiedDate().plusDays(1).isBefore(LocalDateTime.now()))
        .collect(Collectors.toList());
    List<MediaFile> deletedFiles = new ArrayList<>();
    for (MediaFile file : files) {
      try {
        String fbPath = file.getAccount().getId().toString() + "/" + file.getId().toString();
        if (firebaseService.deleteImage(AppConstant.USER_IMAGES + fbPath)) {
          deletedFiles.add(file);
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }
    mediaFileService.deleteMediaFile(deletedFiles);
    logger.info("Finished delete inactive user file - " + deletedFiles.size() + " files deleted");
  }

  @Scheduled(initialDelay = 1000 * 60 * 5, fixedDelay = 1000 * 60 * 5)
  public void deleteDeletingMediaFile() {
    logger.info("Start to delete DeletingMediaFile");
    List<DeletingMediaFile> files = deletingMediaFileRepository.findAll();
    List<DeletingMediaFile> deletedFiles = new ArrayList<>();
    for (DeletingMediaFile file : files) {
      try {
        if (firebaseService.deleteImage(file.getLink())) {
          deletedFiles.add(file);
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }
    deletingMediaFileRepository.deleteAll(deletedFiles);
    logger.info("Finished delete DeletingMediaFile - " + deletedFiles.size() + " files deleted");
  }

}
