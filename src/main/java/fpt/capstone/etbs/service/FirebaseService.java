package fpt.capstone.etbs.service;

import org.springframework.web.multipart.MultipartFile;

public interface FirebaseService {

  String createImage(String path, MultipartFile image, String name) throws Exception;

  boolean deleteImage(String path, String image) throws Exception;
}
