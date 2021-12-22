package com.github.codeteapot.tools.artifact;

@SuppressWarnings("serial")
class UncheckedArtifactRepositoryException extends RuntimeException {

  UncheckedArtifactRepositoryException(ArtifactRepositoryException cause) {
    super(cause);
  }

  @Override
  public ArtifactRepositoryException getCause() {
    return (ArtifactRepositoryException) super.getCause();
  }
}
