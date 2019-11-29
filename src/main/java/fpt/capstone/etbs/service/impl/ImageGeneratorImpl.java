package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.component.ChromeDriverEx;
import fpt.capstone.etbs.service.ImageGenerator;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

@Service
public class ImageGeneratorImpl implements ImageGenerator {

  @Override
  public BufferedImage generateImageFromHtml(String html) throws Exception {
//    System.setProperty("webdriver.chrome.driver", "chromedriver");
    ChromeOptions chromeOptions = new ChromeOptions();
//    chromeOptions.setBinary("/usr/bin/google-chrome");
    chromeOptions.setHeadless(true);

    ChromeDriverEx driver = new ChromeDriverEx(chromeOptions);

      driver.get("data:text/html;charset=utf-8," + html);
    ExpectedCondition<Boolean> pageLoadCondition = driver1 -> ((JavascriptExecutor) driver1)
        .executeScript("return document.readyState").equals("complete");
    WebDriverWait wait = new WebDriverWait(driver, 30);
    wait.until(pageLoadCondition);

    File screenshot = driver.getFullScreenshotAs(OutputType.FILE);
    BufferedImage bi = ImageIO.read(screenshot);
    driver.quit();

    return bi;
  }
}
