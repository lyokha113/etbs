package fpt.capstone.etbs.schedule;

import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.MediaFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Configuration
@EnableScheduling
public class MediaFileSchedule {

    private final Logger logger = LoggerFactory.getLogger(MediaFileSchedule.class);

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private MediaFileService mediaFileService;

    @Scheduled(fixedDelay = 1000 * 60 * 10, initialDelay = 1000 * 30 * 1000)
    public void sendEmail() {
        logger.info("Start to delete inactive file");
        List<MediaFile> files = mediaFileService.getInactiveMediaFiles();
        files.forEach(f -> {
            try {
                firebaseService.deleteImage(f.getAccount().getId().toString(), f.getId().toString());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
        mediaFileService.deleteMediaFile(files);
        logger.info("Finished delete inactive file");
    }
}
