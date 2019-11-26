//package fpt.capstone.etbs;
//
//import fpt.capstone.etbs.component.ChromeDriverEx;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import javax.imageio.ImageIO;
//import org.junit.Test;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.support.ui.ExpectedCondition;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//public class HeadlessChrome {
//  @Test
//  public void createChromeDriverHeadless() throws Exception
//  {
//    //    System.setProperty("webdriver.chrome.driver",
////        "chromedriver");
//    ChromeOptions chromeOptions = new ChromeOptions();
////    chromeOptions.setBinary("/usr/bin/google-chrome");
////    chromeOptions.setHeadless(true);
//
////      WebDriver driver = new ChromeDriver(chromeOptions);
//    ChromeDriverEx driver = new ChromeDriverEx(chromeOptions);
//    driver.get("https://www.howtogeek.com/423558/how-to-take-full-page-screenshots-in-google-chrome-without-using-an-extension/");
//    ExpectedCondition<Boolean> pageLoadCondition = new
//        ExpectedCondition<Boolean>() {
//          public Boolean apply(WebDriver driver) {
//            return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
//          }
//        };
//    WebDriverWait wait = new WebDriverWait(driver, 30);
//    wait.until(pageLoadCondition);
////      driver.get("data:text/html;charset=utf-8," + tutorial.getContent());
//
////      File file = driver.getFullScreenshotAs(OutputType.BASE64);
////      driver.manage().window().setSize(new Dimension(1716,1405));
//
////      Screenshot myScreenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(10000)).takeScreenshot(driver);
////      ByteArrayOutputStream out = new ByteArrayOutputStream();
////      ImageIO.write(myScreenshot.getImage(), "PNG", out);
////      byte[] bytes = out.toByteArray();
//
////      String base64bytes = Base64.encode(bytes);
//    File screenshot = driver.getFullScreenshotAs(OutputType.FILE);
//    BufferedImage bi = ImageIO.read(screenshot);
//    ImageIO.write(bi, "png", new File("test.png"));
//    driver.quit();
//  }
//}
