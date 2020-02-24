package fpt.capstone.etbs.service;

import java.awt.image.BufferedImage;

public interface ImageGenerator {

  BufferedImage generateImageFromHtml(String html) throws Exception;

  BufferedImage generateImageFromHtmlByChrome(String html) throws Exception;

  BufferedImage generateImageFromHtmlByPDFCrowd(String html) throws Exception;
}
