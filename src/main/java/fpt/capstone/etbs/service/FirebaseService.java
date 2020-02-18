package fpt.capstone.etbs.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FirebaseService {

  String createUserImage(MultipartFile file, String path, String name) throws Exception;

  String createUserAvatar(BufferedImage file, String id) throws IOException;

  String createTutorialThumbnail(MultipartFile file, String name) throws Exception;

  String createRawThumbnail(BufferedImage file, String name) throws Exception;

  String createTemplateThumbnail(BufferedImage file, String name) throws Exception;

  String createTemplateImages(String file, String name) throws Exception;

  boolean deleteImage(String fbPath) throws Exception;


}
