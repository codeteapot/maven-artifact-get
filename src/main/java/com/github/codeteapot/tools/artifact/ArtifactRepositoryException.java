package com.github.codeteapot.tools.artifact;

/**
 * Exception occurred when trying to resolve an artifact through a repository.
 */
public class ArtifactRepositoryException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception with a message only.
   *
   * @param message The message.
   */
  public ArtifactRepositoryException(String message) {
    super(message);
  }

  /**
   * Exception with a cause only.
   *
   * @param cause The cause.
   */
  public ArtifactRepositoryException(Throwable cause) {
    super(cause);
  }
}
