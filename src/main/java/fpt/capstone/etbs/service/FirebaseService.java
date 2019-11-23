package fpt.capstone.etbs.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FirebaseService {

  String createUserImage(MultipartFile file, String path, String name) throws Exception;

  String createTutorialImage(MultipartFile file, String path, String name) throws Exception;

  String createRawThumbnailFromTemplate(Integer templateId, Integer rawTemplateId) throws Exception;

  String createTemplateThumbnailFromRaw(Integer rawTemplateId, Integer templateId) throws Exception;

  String updateRawTemplateThumbnail(MultipartFile file, String name) throws Exception;

  String createTemplateThumbnail(MultipartFile file, String name) throws Exception;

  String createTutorialThumbnail(MultipartFile file, String name) throws Exception;

  boolean deleteImage(String fbPath) throws Exception;

  String replaceImageFromUserContent(String html, String order) throws IOException;

  String createTemplateImagesFromUserImage(String url, String id) throws Exception;

}
