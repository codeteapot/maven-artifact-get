package com.github.codeteapot.tools.artifact;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Set;

/**
 * Contains those necessary elements to know where download an artifact from, and its dependencies
 * through {@link ArtifactRepository}.
 */
public class Artifact {

  private final URL location;
  private final Set<ArtifactCoordinates> dependencies;

  Artifact(URL location, Set<ArtifactCoordinates> dependencies) {
    this.location = requireNonNull(location);
    this.dependencies = unmodifiableSet(dependencies);
  }

  /**
   * Location where an artifact can be fetched from.
   * 
   * <p>The same URL stream handler of the repository from this artifact has got is used.
   *
   * @return The artifact location.
   *
   * @see ArtifactRepository
   * @see URLStreamHandler
   */
  public URL getLocation() {
    return location;
  }

  /**
   * Coordinates of {@code compile} and {@code runtime} dependencies.
   *
   * @return The needed artifact dependencies coordinates.
   */
  public Set<ArtifactCoordinates> getDependencies() {
    return dependencies;
  }
}
