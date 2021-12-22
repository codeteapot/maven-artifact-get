package com.github.codeteapot.tools.artifact;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArtifactCoordinatesTest {

  private static final String SOME_GROUP_ID = "some.group";
  private static final String SOME_ARTIFACT_ID = "some-artifact";
  private static final String SOME_VERSION = "some-version";
  
  private static final String ANOTHER_GROUP_ID = "another.group";
  private static final String ANOTHER_ARTIFACT_ID = "another-artifact";
  private static final String ANOTHER_VERSION = "another-version";

  private ArtifactCoordinates coordinates;

  @BeforeEach
  public void setUp() {
    coordinates = new ArtifactCoordinates(SOME_GROUP_ID, SOME_ARTIFACT_ID, SOME_VERSION);
  }

  @Test
  public void equalByObjectReference() {
    ArtifactCoordinates anotherCoordinates = coordinates;

    boolean equals = coordinates.equals(anotherCoordinates);

    assertThat(equals).isTrue();
  }
  
  @Test
  public void equalByObjectFields() {
    ArtifactCoordinates anotherCoordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        SOME_ARTIFACT_ID,
        SOME_VERSION);

    boolean equals = coordinates.equals(anotherCoordinates);

    assertThat(equals).isTrue();
  }

  @Test
  public void notEqualByObjectType() {
    Object anotherObject = new Object();

    boolean equals = coordinates.equals(anotherObject);

    assertThat(equals).isFalse();
  }
  
  @Test
  public void notEqualByGroupId() {
    ArtifactCoordinates anotherCoordinates = new ArtifactCoordinates(
        ANOTHER_GROUP_ID,
        SOME_ARTIFACT_ID,
        SOME_VERSION);

    boolean equals = coordinates.equals(anotherCoordinates);

    assertThat(equals).isFalse();
  }
  
  @Test
  public void notEqualByArtifactId() {
    ArtifactCoordinates anotherCoordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        ANOTHER_ARTIFACT_ID,
        SOME_VERSION);

    boolean equals = coordinates.equals(anotherCoordinates);

    assertThat(equals).isFalse();
  }
  
  @Test
  public void notEqualByVersion() {
    ArtifactCoordinates anotherCoordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        SOME_ARTIFACT_ID,
        ANOTHER_VERSION);

    boolean equals = coordinates.equals(anotherCoordinates);

    assertThat(equals).isFalse();
  }
}
