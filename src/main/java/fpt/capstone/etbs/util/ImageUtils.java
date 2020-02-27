package fpt.capstone.etbs.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

public class ImageUtils {

  public static BufferedImage base64ToImage(String base64) throws IOException {
    String base64Image = base64.split(",")[1];
    byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image);
    return ImageIO.read(new ByteArrayInputStream(imageBytes));
  }

  public static ByteArrayOutputStream urlToImage(URL url) throws IOException {
    BufferedImage image = ImageIO.read(url);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(image, "png", os);
    os.flush();
    return os;
  }

  public static ByteArrayOutputStream writeImageData(BufferedImage image) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(image, "png", os);
    os.flush();
    return os;
  }

}
