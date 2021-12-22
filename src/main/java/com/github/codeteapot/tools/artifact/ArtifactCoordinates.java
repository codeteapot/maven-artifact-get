package com.github.codeteapot.tools.artifact;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Coordinates used to identify an artifact.
 */
public class ArtifactCoordinates implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String groupId;
  private final String artifactId;
  private final String version;

  /**
   * Coordinates with given group, artifact identifier and version.
   *
   * @param groupId Group.
   * @param artifactId Artifact identifier.
   * @param version Version.
   */
  public ArtifactCoordinates(String groupId, String artifactId, String version) {
    this.groupId = requireNonNull(groupId);
    this.artifactId = requireNonNull(artifactId);
    this.version = requireNonNull(version);
  }

  String getPath(String extension) {
    return concat(
        Stream.of(groupId.split("\\.")),
        Stream.of(artifactId, version, format("%s-%s.%s", artifactId, version, extension)))
            .collect(joining("/", "/", ""));

  }

  /**
   * Based on artifact identifier hash.
   */
  @Override
  public int hashCode() {
    return artifactId.hashCode();
  }

  /**
   * Based on compounded equality of group, artifact identifier and version.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ArtifactCoordinates) {
      ArtifactCoordinates coordinates = (ArtifactCoordinates) obj;
      return groupId.equals(coordinates.groupId)
          && artifactId.equals(coordinates.artifactId)
          && version.equals(coordinates.version);
    }
    return false;
  }
}
