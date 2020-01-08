package fpt.capstone.etbs.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.service.FirebaseService;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseServiceImpl implements FirebaseService {

  @Autowired
  private Bucket bucket;

  @Override
  public String createUserImage(MultipartFile file, String path, String name) throws Exception {
    String fbPath = AppConstant.USER_IMAGES + path + "/" + name;
    return createImage(fbPath, file);
  }

  @Override
  public String createUserAvatar(BufferedImage file, String id) throws IOException {
    String fbPath = AppConstant.USER_IMAGES + id + "/" + "Avatar";
    return createImage(fbPath, file);
  }

  @Override
  public String createTutorialThumbnail(MultipartFile file, String name)
      throws Exception {
    String fbPath = AppConstant.TUTORIAL_THUMBNAIL + name;
    return createImage(fbPath, file);
  }

  @Override
  public String createRawThumbnail(BufferedImage file, String name) throws Exception {
    String fbPath = AppConstant.RAW_TEMPLATE_THUMBNAIL + name;
    return createImage(fbPath, file);
  }

  @Override
  public String createTemplateThumbnail(BufferedImage file, String name)
      throws Exception {
    String fbPath = AppConstant.TEMPLATE_THUMBNAIL + name;
    return createImage(fbPath, file);
  }

  @Override
  public String createTemplateImages(String file, String name) throws Exception {
    String fbPath = AppConstant.TEMPLATE_IMAGE + name;
    return createImage(fbPath, file);
  }

  @Override
  public boolean deleteImage(String fbPath) {
    Storage storage = bucket.getStorage();
    BlobId blobId = BlobId.of(bucket.getName(), fbPath);
    return storage.delete(blobId);
  }

  private String createImage(String fbPath, BufferedImage image) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(image, "png", os);
    os.flush();
    Storage storage = bucket.getStorage();
    BlobId blobId = BlobId.of(bucket.getName(), fbPath);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png").build();
    Blob blob = storage.create(blobInfo, os.toByteArray());
    URL url = blob.signUrl(365, TimeUnit.DAYS);
    return url.toString();
  }

  private String createImage(String fbPath, String image) {
    BlobId blobId = BlobId.of(bucket.getName(), image);
    Storage storage = bucket.getStorage();
    Blob blob = storage.get(blobId);
    blobId = BlobId.of(bucket.getName(), fbPath);
    blob.copyTo(blobId);
    blob = storage.get(blobId);
    URL url = blob.signUrl(365, TimeUnit.DAYS);
    return url.toString();
  }

  private String createImage(String fbPath, MultipartFile image) throws Exception {
    Storage storage = bucket.getStorage();
    String mime = image.getContentType();
    if (mime == null || (!mime.equals("image/png") && !mime.equals("image/jpeg"))) {
      throw new BadRequestException("File not supported");
    }
    BlobId blobId = BlobId.of(bucket.getName(), fbPath);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(mime).build();
    Blob blob = storage.create(blobInfo, image.getBytes());
    URL url = blob.signUrl(365, TimeUnit.DAYS);
    return url.toString();
  }
}
