package fpt.capstone.etbs.service;

import java.awt.image.BufferedImage;

public interface ImageGenerator {
  BufferedImage generateImageFromHtml(String html) throws Exception;
}
