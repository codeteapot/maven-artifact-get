package com.github.codeteapot.tools.artifact;

import static com.github.codeteapot.tools.artifact.TestUtil.validURL;
import static java.nio.file.Files.write;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.stream.Stream;
import javax.xml.bind.JAXBException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ArtifactRepositoryTest {

  private static final String TEST_PROTOCOL = "file";
  private static final String TEST_HOST = "";
  private static final int TEST_PORT = -1;

  private static final URL ANY_DIRECTORY = validURL("file:///any-directory");

  private static final URL SOME_DIRECTORY = validURL("file:///some-directory");

  private static final int SOME_DIRECTORY_BASED_HASH_CODE = 491834430;

  private static final URL ANOTHER_DIRECTORY = validURL("file:///another-directory");

  private static final ArtifactCoordinates SOME_ARTIFACT_COORDINATES = new ArtifactCoordinates(
      "some.group",
      "some-artifact",
      "some-version");
  private static final String SOME_ARTIFACT_SUBPATH = "some/group/some-artifact/some-version";
  private static final String SOME_ARTIFACT_POM_PATH = "some-artifact-some-version.pom";
  private static final String SOME_ARTIFACT_BUNDLE_PATH = "some-artifact-some-version.jar";

  private static final ArtifactCoordinates SOME_DEPENDENCY_ARTIFACT_COORDINATES =
      new ArtifactCoordinates(
          "some.dependency.group",
          "some-dependency-artifact",
          "some-dependency-version");
  private static final String SOME_DEPENDENCY_GROUP_ID = "some.dependency.group";
  private static final String SOME_DEPENDENCY_ARTIFACT_ID = "some-dependency-artifact";
  private static final String SOME_DEPENDENCY_VERSION = "some-dependency-version";

  private static final ArtifactCoordinates SOME_RUNTIME_DEPENDENCY_ARTIFACT_COORDINATES =
      new ArtifactCoordinates(
          "some.runtime.dependency.group",
          "some-runtime-dependency-artifact",
          "some-runtime-dependency-version");
  private static final String SOME_RUNTIME_DEPENDENCY_GROUP_ID = "some.runtime.dependency.group";
  private static final String SOME_RUNTIME_DEPENDENCY_ARTIFACT_ID =
      "some-runtime-dependency-artifact";
  private static final String SOME_RUNTIME_DEPENDENCY_VERSION = "some-runtime-dependency-version";

  @Test
  public void hashCodeBasedOnDirectory() {
    ArtifactRepository repository = new ArtifactRepository(SOME_DIRECTORY);

    int hashCode = repository.hashCode();

    assertThat(hashCode).isEqualTo(SOME_DIRECTORY_BASED_HASH_CODE);
  }

  @Test
  public void equalByObjectReference() {
    ArtifactRepository repository = new ArtifactRepository(ANY_DIRECTORY);
    ArtifactRepository anotherRepository = repository;

    boolean equals = repository.equals(anotherRepository);

    assertThat(equals).isTrue();
  }

  @Test
  public void equalByDirectory() {
    ArtifactRepository repository = new ArtifactRepository(SOME_DIRECTORY);
    ArtifactRepository anotherRepository = new ArtifactRepository(SOME_DIRECTORY);

    boolean equals = repository.equals(anotherRepository);

    assertThat(equals).isTrue();
  }

  @Test
  public void notEqualByObjectType() {
    ArtifactRepository repository = new ArtifactRepository(ANY_DIRECTORY);
    Object anotherObject = new Object();

    boolean equals = repository.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualByDirectory() {
    ArtifactRepository repository = new ArtifactRepository(SOME_DIRECTORY);
    ArtifactRepository anotherRepository = new ArtifactRepository(ANOTHER_DIRECTORY);

    boolean equals = repository.equals(anotherRepository);

    assertThat(equals).isFalse();
  }

  @Test
  @Tag("integration")
  public void getExpectedScopeDependencies(@TempDir File someRepositoryDir) throws Exception {
    ArtifactRepository repository = new ArtifactRepository(someRepositoryDir.toURI().toURL());
    File artifactDir = new File(
        someRepositoryDir.getAbsoluteFile(),
        SOME_ARTIFACT_SUBPATH);
    artifactDir.mkdirs();
    write(
        Paths.get(artifactDir.getAbsolutePath(), SOME_ARTIFACT_POM_PATH),
        Stream.of(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">",
            "  <dependencies>",
            "    <dependency>",
            "      <groupId>" + SOME_DEPENDENCY_GROUP_ID + "</groupId>",
            "      <artifactId>" + SOME_DEPENDENCY_ARTIFACT_ID + "</artifactId>",
            "      <version>" + SOME_DEPENDENCY_VERSION + "</version>",
            "    </dependency>",
            "    <dependency>",
            "      <groupId>" + SOME_RUNTIME_DEPENDENCY_GROUP_ID + "</groupId>",
            "      <artifactId>" + SOME_RUNTIME_DEPENDENCY_ARTIFACT_ID + "</artifactId>",
            "      <version>" + SOME_RUNTIME_DEPENDENCY_VERSION + "</version>",
            "      <scope>runtime</scope>",
            "    </dependency>",
            "    <dependency>",
            "      <groupId>test.dependency.group</groupId>",
            "      <artifactId>test.dependency.artifact</artifactId>",
            "      <version>test-dependency-version</version>",
            "      <scope>test</scope>",
            "    </dependency>",
            "  </dependencies>",
            "</project>")
            .collect(toList()),
        CREATE_NEW);

    Artifact artifact = repository.get(SOME_ARTIFACT_COORDINATES);

    assertThat(artifact.getLocation()).satisfies(location -> {
      assertThat(location.getProtocol()).isEqualTo(TEST_PROTOCOL);
      assertThat(location.getHost()).isEqualTo(TEST_HOST);
      assertThat(location.getPort()).isEqualTo(TEST_PORT);
      assertThat(Paths.get(location.getPath())).satisfies(path -> {
        assertThat(path.getParent()).isEqualTo(artifactDir.toPath());
        assertThat(path.getFileName()).isEqualTo(Paths.get(SOME_ARTIFACT_BUNDLE_PATH));
      });
    });
    assertThat(artifact.getDependencies()).containsExactlyInAnyOrder(
        SOME_DEPENDENCY_ARTIFACT_COORDINATES,
        SOME_RUNTIME_DEPENDENCY_ARTIFACT_COORDINATES);
  }

  @Test
  @Tag("integration")
  public void getWithParentVersionOnDependency(@TempDir File someRepositoryDir) throws Exception {
    ArtifactRepository repository = new ArtifactRepository(someRepositoryDir.toURI().toURL());
    File artifactDir = new File(
        someRepositoryDir.getAbsoluteFile(),
        SOME_ARTIFACT_SUBPATH);
    artifactDir.mkdirs();
    write(
        Paths.get(artifactDir.getAbsolutePath(), SOME_ARTIFACT_POM_PATH),
        Stream.of(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">",
            "  <parent>",
            "    <version>" + SOME_DEPENDENCY_VERSION + "</version>",
            "  </parent>",
            "  <dependencies>",
            "    <dependency>",
            "      <groupId>" + SOME_DEPENDENCY_GROUP_ID + "</groupId>",
            "      <artifactId>" + SOME_DEPENDENCY_ARTIFACT_ID + "</artifactId>",
            "    </dependency>",
            "  </dependencies>",
            "</project>")
            .collect(toList()),
        CREATE_NEW);

    Artifact artifact = repository.get(SOME_ARTIFACT_COORDINATES);

    assertThat(artifact.getDependencies()).containsExactly(SOME_DEPENDENCY_ARTIFACT_COORDINATES);
  }

  @Test
  @Tag("integration")
  public void failWhenDependencyVersionIsNotKnown(@TempDir File someRepositoryDir)
      throws Exception {
    ArtifactRepository repository = new ArtifactRepository(someRepositoryDir.toURI().toURL());
    File artifactDir = new File(
        someRepositoryDir.getAbsoluteFile(),
        SOME_ARTIFACT_SUBPATH);
    artifactDir.mkdirs();
    write(
        Paths.get(artifactDir.getAbsolutePath(), SOME_ARTIFACT_POM_PATH),
        Stream.of(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">",
            "  <parent>",
            "  </parent>",
            "  <dependencies>",
            "    <dependency>",
            "      <groupId>" + SOME_DEPENDENCY_GROUP_ID + "</groupId>",
            "      <artifactId>" + SOME_DEPENDENCY_ARTIFACT_ID + "</artifactId>",
            "    </dependency>",
            "  </dependencies>",
            "</project>")
            .collect(toList()),
        CREATE_NEW);

    Throwable e = catchThrowable(() -> repository.get(SOME_ARTIFACT_COORDINATES));

    assertThat(e)
        .isInstanceOf(ArtifactRepositoryException.class)
        .hasMessage("Unknown artifact version");
  }

  @Test
  @Tag("integration")
  public void failWhenProjectFileIsInvalid(@TempDir File someRepositoryDir) throws Exception {
    ArtifactRepository repository = new ArtifactRepository(someRepositoryDir.toURI().toURL());
    File artifactDir = new File(
        someRepositoryDir.getAbsoluteFile(),
        SOME_ARTIFACT_SUBPATH);
    artifactDir.mkdirs();
    write(
        Paths.get(artifactDir.getAbsolutePath(), SOME_ARTIFACT_POM_PATH),
        Stream.of(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<unexpected/>")
            .collect(toList()),
        CREATE_NEW);

    Throwable e = catchThrowable(() -> repository.get(SOME_ARTIFACT_COORDINATES));

    assertThat(e)
        .isInstanceOf(ArtifactRepositoryException.class)
        .hasCauseInstanceOf(JAXBException.class);
  }
}
