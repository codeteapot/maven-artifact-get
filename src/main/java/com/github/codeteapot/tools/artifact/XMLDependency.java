package com.github.codeteapot.tools.artifact;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.xml.bind.annotation.XmlElement;

class XMLDependency {

  private static final Set<String> REQUIRED_SCOPES = Stream.of("compile", "runtime")
      .collect(toSet());

  @XmlElement(
      namespace = "http://maven.apache.org/POM/4.0.0",
      name = "groupId")
  private String groupId;

  @XmlElement(
      namespace = "http://maven.apache.org/POM/4.0.0",
      name = "artifactId")
  private String artifactId;

  @XmlElement(
      namespace = "http://maven.apache.org/POM/4.0.0",
      name = "version")
  private String version;

  @XmlElement(
      namespace = "http://maven.apache.org/POM/4.0.0",
      name = "scope")
  private String scope;

  private XMLDependency() {
    groupId = null;
    artifactId = null;
    version = null;
  }

  ArtifactCoordinates toRequiredDependency(XMLParent parent) {
    if (scope == null || REQUIRED_SCOPES.contains(scope)) {
      return new ArtifactCoordinates(
          groupId,
          artifactId,
          ofNullable(version).orElseGet(versionFrom(parent)));
    }
    return null;
  }

  private Supplier<String> versionFrom(XMLParent parent) {
    return () -> ofNullable(parent)
        .map(XMLParent::getVersion)
        .orElseThrow(() -> new UncheckedArtifactRepositoryException(
            new ArtifactRepositoryException("Unknown artifact version")));
  }
}
