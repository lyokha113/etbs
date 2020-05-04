package fpt.capstone.etbs.config;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.model.DeletingMediaFile;
import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.Notification;
import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.repository.DeletingMediaFileRepository;
import fpt.capstone.etbs.repository.MediaFileRepository;
import fpt.capstone.etbs.repository.NotificationRepository;
import fpt.capstone.etbs.repository.PublishRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.MediaFileService;
import fpt.capstone.etbs.service.NotificationService;
import fpt.capstone.etbs.service.PublishService;
import fpt.capstone.etbs.service.TemplateService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Configuration
@EnableScheduling
@Slf4j
public class BackgroundScheduleConfig {

  @Autowired
  private FirebaseService firebaseService;

  @Autowired
  private MediaFileService mediaFileService;

  @Autowired
  private TemplateService templateService;

  @Autowired
  private PublishService publishService;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private DeletingMediaFileRepository deletingMediaFileRepository;

  @Autowired
  private MediaFileRepository mediaFileRepository;

  @Autowired
  private PublishRepository publishRepository;

  @Bean
  public TaskScheduler taskScheduler() {
    final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(10);
    return scheduler;
  }

  @Scheduled(initialDelay = 1000 * 60 * 5, fixedDelay = 1000 * 60 * 5)
  public void deleteInactiveUserFile() {
    log.info("Start to delete inactive user file");
    List<MediaFile> files = mediaFileService.getInactiveMediaFiles();
    files = files.stream()
        .filter(f -> f.getLastModifiedDate().plusDays(1).isBefore(LocalDateTime.now()))
        .collect(Collectors.toList());
    List<MediaFile> deletedFiles = new ArrayList<>();
    for (MediaFile file : files) {
      try {
        String fbPath = file.getAccount().getId().toString() + "/" + file.getId().toString();
        firebaseService.deleteImage(AppConstant.USER_IMAGES + fbPath);
        deletedFiles.add(file);
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }
    mediaFileRepository.deleteAll(deletedFiles);
    log.info("Finished delete inactive user file - " + deletedFiles.size() + " files deleted");
  }

  @Scheduled(initialDelay = 1000 * 60, fixedDelay = 1000 * 60 * 5)
  public void deleteDeletingMediaFile() {
    log.info("Start to delete DeletingMediaFile");
    List<DeletingMediaFile> files = deletingMediaFileRepository.findAll();
    List<DeletingMediaFile> deletedFiles = new ArrayList<>();
    for (DeletingMediaFile file : files) {
      try {
        firebaseService.deleteImage(file.getLink());
        deletedFiles.add(file);
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }
    deletingMediaFileRepository.deleteAll(deletedFiles);
    log.info("Finished delete DeletingMediaFile - " + deletedFiles.size() + " files deleted");
  }

  @Scheduled(initialDelay = 1000 * 60 * 5, fixedDelay = 1000 * 60 * 5)
  public void calculateTemplateScore() {
    log.info("Start to calculate template score");
    templateService.calculateScore();
  }

  @Scheduled(initialDelay = 1000 * 60 * 5, fixedDelay = 1000 * 60 * 5)
  public void deleteNotifications() {
    log.info("Start to delete 3 days loaded notifications");
    List<Notification> notifications = notificationService.getNotificationsToRemove();
    notificationRepository.deleteAll(notifications);
    log.info("Finished delete 3 days loaded notifications - " + notifications.size()
        + " notifications deleted");
  }

  @Scheduled(initialDelay = 1000 * 60 * 5, fixedDelay = 1000 * 60 * 5)
  @Transactional
  public void deletePublishes() {
    log.info("Start to delete 30 days processed publishes");
    List<Publish> publishes = publishService.getPublishesToRemove();
    publishRepository.deleteAll(publishes);
    log.info("Finished delete 30 days processed publishes - " + publishes.size()
        + " notifications deleted");
  }

}
