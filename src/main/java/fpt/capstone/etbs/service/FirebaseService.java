package fpt.capstone.etbs.service;

import org.springframework.web.multipart.MultipartFile;

public interface FirebaseService {

  String createUserImage(MultipartFile file, String path, String name) throws Exception;

  String createTutorialImage(MultipartFile file, String path, String name) throws Exception;

  String createRawTemplateThumbnail(MultipartFile file, String path, String name) throws Exception;

  String createTemplateThumbnail(MultipartFile file, String path, String name) throws Exception;

  String createTutorialThumbnail(MultipartFile file, String path, String name) throws Exception;

  boolean deleteImage(String fbPath) throws Exception;
}
