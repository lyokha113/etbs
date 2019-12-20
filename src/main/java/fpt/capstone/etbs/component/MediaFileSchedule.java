package fpt.capstone.etbs.component;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.payload.PublishResponse;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.MediaFileService;
import fpt.capstone.etbs.service.PublishService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableScheduling
public class MediaFileSchedule {

  private final Logger logger = LoggerFactory.getLogger(MediaFileSchedule.class);

  @Autowired
  private FirebaseService firebaseService;

  @Autowired
  private MediaFileService mediaFileService;


  @Scheduled(fixedDelay = 1000 * 60, initialDelay = 1000 * 60 * 5)
//  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
  public void deleteInactiveUserFile() {
    logger.info("Start to delete inactive user file");
    List<MediaFile> deleteFiles = mediaFileService.getInactiveMediaFiles();
    deleteFiles = deleteFiles.stream()
        .filter(f -> f.getLastModifiedDate().plusDays(1).isBefore(LocalDateTime.now()))
        .collect(Collectors.toList());
    List<MediaFile> deletedFiles = new ArrayList<>();
    for (MediaFile file : deleteFiles) {
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

}
