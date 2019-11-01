package fpt.capstone.etbs.schedule;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.MediaFileService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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

  @Scheduled(fixedDelay = 1000 * 60 * 5, initialDelay = 1000 * 60 * 5)
  public void deleteInactiveFile() {
    logger.info("Start to delete inactive file");
    List<MediaFile> deleteFiles = mediaFileService.getInactiveMediaFiles();
    deleteFiles = deleteFiles.stream()
        .filter(f -> {
          LocalDateTime now = LocalDateTime.now();
          LocalDateTime deleteTime =
              LocalDateTime.ofInstant(
                  f.getLastModifiedDate().toInstant(), ZoneId.systemDefault());
          return deleteTime.plusDays(1).isAfter(now);
        })
        .collect(Collectors.toList());

    List<MediaFile> deletedFiles = new ArrayList<>();

    deleteFiles.forEach(
        f -> {
          try {
            String fbPath = f.getAccount().getId().toString() + "/" + f.getId().toString();
            boolean deleted = firebaseService.deleteImage(AppConstant.USER_IMAGES + fbPath);
            if (deleted) {
              deletedFiles.add(f);
            }
          } catch (Exception e) {
            logger.error(e.getMessage());
          }
        });
    mediaFileService.deleteMediaFile(deletedFiles);
    logger.info("Finished delete inactive file");
  }
}
