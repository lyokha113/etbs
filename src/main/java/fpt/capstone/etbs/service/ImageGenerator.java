package fpt.capstone.etbs.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageGenerator {

  BufferedImage generateImageFromHtml(String html) throws Exception;
}
