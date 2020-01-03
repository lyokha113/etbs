package fpt.capstone.etbs.service;

import java.awt.image.BufferedImage;
import org.springframework.web.multipart.MultipartFile;

public interface FirebaseService {

  String createUserImage(MultipartFile file, String path, String name) throws Exception;

  String createTutorialThumbnail(MultipartFile file, String name) throws Exception;

  String createRawThumbnail(BufferedImage file, String name) throws Exception;

  String createTemplateThumbnail(BufferedImage file, String name) throws Exception;

  String createTemplateImages(String file, String name) throws Exception;

  boolean deleteImage(String fbPath) throws Exception;

}
