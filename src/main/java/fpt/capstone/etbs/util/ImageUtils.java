package fpt.capstone.etbs.util;

import fpt.capstone.etbs.constant.AppConstant;
import java.awt.Graphics2D;
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

  public static BufferedImage resizeThumbnail(BufferedImage in) {
    int scaledWidth = in.getWidth();
    int scaledHeight = in.getHeight();
    if (scaledWidth > AppConstant.MAX_WIDTH_TEMPLATE_THUMBNAIL) {
      scaledHeight = (int) (scaledHeight / (scaledWidth/ AppConstant.MAX_WIDTH_TEMPLATE_THUMBNAIL));
      scaledWidth = (int) AppConstant.MAX_WIDTH_TEMPLATE_THUMBNAIL;
      BufferedImage out = new BufferedImage(scaledWidth, scaledHeight, in.getType());
      Graphics2D g2d = out.createGraphics();
      g2d.drawImage(in, 0, 0, scaledWidth, scaledHeight, null);
      g2d.dispose();
      return out;
    }
    return in;
  }

}
