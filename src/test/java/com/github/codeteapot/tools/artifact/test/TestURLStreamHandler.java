package com.github.codeteapot.tools.artifact.test;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Objects;

public class TestURLStreamHandler extends URLStreamHandler {

  private String name;

  @ConstructorProperties("name")
  public TestURLStreamHandler(String name) {
    this.name = Objects.requireNonNull(name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof TestURLStreamHandler) {
      TestURLStreamHandler handler = (TestURLStreamHandler) obj;
      return name.equals(handler.name);
    }
    return false;
  }

  @Override
  protected URLConnection openConnection(URL u) throws IOException {
    throw new UsingTestURLStreamHandlerException();
  }
}
