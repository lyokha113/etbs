package fpt.capstone.etbs.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import fpt.capstone.etbs.service.FirebaseService;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseServiceImpl implements FirebaseService {

  private static final String USER_IMAGES = "userImages/";

  @Autowired
  private Bucket bucket;

  @Override
  public String createImage(String path, MultipartFile image, String name) throws Exception {
    Storage storage = bucket.getStorage();
    BlobId blobId = BlobId.of(bucket.getName(), USER_IMAGES + path + "/" + name);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
        .setContentType(image.getContentType())
        .build();
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
