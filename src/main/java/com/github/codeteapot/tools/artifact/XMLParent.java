package com.github.codeteapot.tools.artifact;

import javax.xml.bind.annotation.XmlElement;

class XMLParent {

  @XmlElement(
      namespace = "http://maven.apache.org/POM/4.0.0",
      name = "version")
  private String version;

  private XMLParent() {
    version = null;
  }

  String getVersion() {
    return version;
  }
}
