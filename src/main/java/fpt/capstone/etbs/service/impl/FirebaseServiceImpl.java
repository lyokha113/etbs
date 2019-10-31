package fpt.capstone.etbs.service.impl;

import com.google.cloud.storage.*;
import fpt.capstone.etbs.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseServiceImpl implements FirebaseService {

  private static final String USER_IMAGES = "userImages/";

  @Autowired private Bucket bucket;

  @Override
  public String createImage(String path, MultipartFile image, String name) throws Exception {
    Storage storage = bucket.getStorage();
    BlobId blobId = BlobId.of(bucket.getName(), USER_IMAGES + path + "/" + name);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(image.getContentType()).build();
    Blob blob = storage.create(blobInfo, image.getBytes());
    URL url = blob.signUrl(365, TimeUnit.DAYS);
    return url.toString();
  }

  @Override
  public boolean deleteImage(String path, String image) {
    Storage storage = bucket.getStorage();
    BlobId blobId = BlobId.of(bucket.getName(), USER_IMAGES + path + "/" + image);
    return storage.delete(blobId);
  }
}
