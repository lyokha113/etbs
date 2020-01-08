package fpt.capstone.etbs.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

public class ImageUtils {

  public static BufferedImage base64ToImage(String base64) throws IOException {
    String base64Image = base64.split(",")[1];
    byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image);
    return ImageIO.read(new ByteArrayInputStream(imageBytes));
  }

}
