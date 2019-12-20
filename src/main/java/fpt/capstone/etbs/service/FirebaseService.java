package fpt.capstone.etbs.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FirebaseService {

  String createUserImage(MultipartFile file, String path, String name) throws Exception;

  String createTutorialThumbnail(MultipartFile file, String name) throws Exception;

  String createRawThumbnailFromTemplate(Integer templateId, Integer versionId) throws Exception;

  String createRawThumbnail(MultipartFile file, String name) throws Exception;

  String createTemplateThumbnail(Integer rawTemplateId, Integer templateId) throws Exception;

  String createTemplateThumbnail(BufferedImage file, String name) throws Exception;

  boolean deleteImage(String fbPath) throws Exception;

  String replaceImageFromUserContent(String html, String order) throws IOException;

  String replaceImageFromUserContent(BufferedImage image, String order) throws IOException;

  String createTemplateImagesFromUserImage(String url, String id) throws Exception;

}
