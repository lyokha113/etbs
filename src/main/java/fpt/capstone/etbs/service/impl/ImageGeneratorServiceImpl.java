package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.component.ChromeDriverEx;
import fpt.capstone.etbs.service.ImageGeneratorService;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

@Service
public class ImageGeneratorServiceImpl implements ImageGeneratorService {

  @Override
  public BufferedImage generateImageFromHtml(String html) throws Exception {
    return generateImageFromHtmlByChrome(html);
  }

  @Override
  public BufferedImage generateImageFromHtmlByChrome(String html) throws Exception {

    if (System.getProperty("os.name").toLowerCase().contains("win")) {
      System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
    } else {
      System.setProperty("webdriver.chrome.driver", "chromedriver");
    }

    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setHeadless(true);

    ChromeDriverEx driver = new ChromeDriverEx(chromeOptions);

    driver.get("about:blank");
    WebElement e = driver.findElement(By.tagName("html"));
    String script = "arguments[0].innerHTML='" + StringEscapeUtils.escapeEcmaScript(html) + "'";
    ((JavascriptExecutor) driver).executeScript(script, e);

    ExpectedCondition<Boolean> pageLoadCondition = jsExecutor -> {
      assert jsExecutor != null;
      return ((JavascriptExecutor) jsExecutor)
          .executeScript("return document.readyState").equals("complete");
    };
    WebDriverWait wait = new WebDriverWait(driver, 30);
    wait.until(pageLoadCondition);

    int imageToLoad = Integer.parseInt(((JavascriptExecutor) driver)
        .executeScript("return document.images.length").toString());
    int loaded = 0;
    while (loaded < imageToLoad) {
      for (int i = 0; i < imageToLoad; i++) {
        loaded += (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return document.images[" + i + "].complete;") ? 1 : 0;
      }
      Thread.sleep(100);
    }

    File screenshot = driver.getFullScreenshotAs(OutputType.FILE);
    driver.quit();
    return ImageIO.read(screenshot);
  }

}
