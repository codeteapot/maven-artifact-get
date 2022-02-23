package com.github.codeteapot.tools.artifact;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ArtifactCoordinatesTest {

  private static final String ANY_GROUP_ID = "any.group";
  private static final String ANY_ARTIFACT_ID = "any-artifact";
  private static final String ANY_VERSION = "any-version";

  private static final String SOME_GROUP_ID = "some.group";
  private static final String SOME_ARTIFACT_ID = "some-artifact";
  private static final String SOME_VERSION = "some-version";

  private static final String ANOTHER_GROUP_ID = "another.group";
  private static final String ANOTHER_ARTIFACT_ID = "another-artifact";
  private static final String ANOTHER_VERSION = "another-version";

  @Test
  public void hasGroupId() {
    ArtifactCoordinates coordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        ANY_ARTIFACT_ID,
        ANY_VERSION);

    assertThat(coordinates.getGroupId()).isEqualTo(SOME_GROUP_ID);
  }

  @Test
  public void hasArtifactId() {
    ArtifactCoordinates coordinates = new ArtifactCoordinates(
        ANY_GROUP_ID,
        SOME_ARTIFACT_ID,
        ANY_VERSION);

    assertThat(coordinates.getArtifactId()).isEqualTo(SOME_ARTIFACT_ID);
  }

  @Test
  public void hasVersion() {
    ArtifactCoordinates coordinates = new ArtifactCoordinates(
        ANY_GROUP_ID,
        ANY_ARTIFACT_ID,
        SOME_VERSION);

    assertThat(coordinates.getVersion()).isEqualTo(SOME_VERSION);
  }

  @Test
  public void equalByObjectReference() {
    ArtifactCoordinates coordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        SOME_ARTIFACT_ID,
        SOME_VERSION);
    ArtifactCoordinates anotherCoordinates = coordinates;

    boolean equals = coordinates.equals(anotherCoordinates);

    assertThat(equals).isTrue();
  }

  @Test
  public void equalByObjectFields() {
    ArtifactCoordinates coordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        SOME_ARTIFACT_ID,
        SOME_VERSION);
    ArtifactCoordinates anotherCoordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        SOME_ARTIFACT_ID,
        SOME_VERSION);

    boolean equals = coordinates.equals(anotherCoordinates);

    assertThat(equals).isTrue();
  }

  @Test
  public void notEqualByObjectType() {
    ArtifactCoordinates coordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        SOME_ARTIFACT_ID,
        SOME_VERSION);
    Object anotherObject = new Object();

    boolean equals = coordinates.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualByGroupId() {
    ArtifactCoordinates coordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        SOME_ARTIFACT_ID,
        SOME_VERSION);
    ArtifactCoordinates anotherCoordinates = new ArtifactCoordinates(
        ANOTHER_GROUP_ID,
        SOME_ARTIFACT_ID,
        SOME_VERSION);

    boolean equals = coordinates.equals(anotherCoordinates);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualByArtifactId() {
    ArtifactCoordinates coordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        SOME_ARTIFACT_ID,
        SOME_VERSION);
    ArtifactCoordinates anotherCoordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        ANOTHER_ARTIFACT_ID,
        SOME_VERSION);

    boolean equals = coordinates.equals(anotherCoordinates);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualByVersion() {
    ArtifactCoordinates coordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        SOME_ARTIFACT_ID,
        SOME_VERSION);
    ArtifactCoordinates anotherCoordinates = new ArtifactCoordinates(
        SOME_GROUP_ID,
        SOME_ARTIFACT_ID,
        ANOTHER_VERSION);

    boolean equals = coordinates.equals(anotherCoordinates);

    assertThat(equals).isFalse();
  }
}
