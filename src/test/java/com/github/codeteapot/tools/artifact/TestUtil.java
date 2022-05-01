package com.github.codeteapot.tools.artifact;

import java.net.MalformedURLException;
import java.net.URL;

public class TestUtil {

  private TestUtil() {}

  public static URL validURL(String spec) {
    try {
      return new URL(spec);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
