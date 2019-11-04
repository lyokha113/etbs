package fpt.capstone.etbs.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.service.FirebaseService;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseServiceImpl implements FirebaseService {

  @Autowired
  private Bucket bucket;


  @Override
  public String createUserImage(MultipartFile file, String path, String name) throws Exception {
    String fbPath = AppConstant.RAW_TEMPLATE_THUMBNAIL + path + "/" + name;
    return createImage(fbPath, file);
  }

  @Override
  public String createTutorialImage(MultipartFile file, String path, String name) throws Exception {
    String fbPath = AppConstant.TUTORIAL_IMAGES + path + "/" + name;
    return createImage(fbPath, file);
  }

  @Override
  public String createRawTemplateThumbnail(MultipartFile file, String name)
      throws Exception {
    String fbPath = AppConstant.RAW_TEMPLATE_THUMBNAIL + name;
    return createImage(fbPath, file);
  }

  @Override
  public String createTemplateThumbnail(MultipartFile file, String name)
      throws Exception {
    String fbPath = AppConstant.TEMPLATE_THUMBNAIL + name;
    return createImage(fbPath, file);
  }

  @Override
  public String createTutorialThumbnail(MultipartFile file, String name)
      throws Exception {
    String fbPath = AppConstant.TUTORIAL_THUMBNAIL + name;
    return createImage(fbPath, file);
  }

  @Override
  public boolean deleteImage(String fbPath) {
    Storage storage = bucket.getStorage();
    BlobId blobId = BlobId.of(bucket.getName(), fbPath);
    return storage.delete(blobId);
  }

  private String createImage(String fbPath, MultipartFile image) throws Exception {
    Storage storage = bucket.getStorage();
    BlobId blobId = BlobId.of(bucket.getName(), fbPath);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(image.getContentType()).build();
    Blob blob = storage.create(blobInfo, image.getBytes());
    URL url = blob.signUrl(365, TimeUnit.DAYS);
    return url.toString();
  }
}
