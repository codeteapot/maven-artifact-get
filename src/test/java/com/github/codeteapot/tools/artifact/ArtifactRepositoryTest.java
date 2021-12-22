package com.github.codeteapot.tools.artifact;

import static java.nio.file.Files.write;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.github.codeteapot.tools.artifact.test.TestURLStreamHandler;
import com.github.codeteapot.tools.artifact.test.UsingTestURLStreamHandlerException;
import java.io.File;
import java.net.URLStreamHandler;
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
  private static final URLStreamHandler TEST_HANDLER = null;

  private static final String ANY_PROTOCOL = "any";
  private static final String ANY_HOST = "any";
  private static final int ANY_PORT = 0;
  private static final String ANY_PATH = "any";
  private static final URLStreamHandler ANY_HANDLER = new TestURLStreamHandler("any");

  private static final ArtifactCoordinates ANY_ARTIFACT_COORDINATES = new ArtifactCoordinates(
      "any.group",
      "any-artifact",
      "any-version");

  private static final String SOME_PROTOCOL = "some";
  private static final String SOME_HOST = "some";
  private static final int SOME_PORT = 1;
  private static final String SOME_PATH = "some";
  private static final URLStreamHandler SOME_HANDLER = new TestURLStreamHandler("some");

  private static final int SOME_HOST_BASED_HASH_CODE = 3536116;

  private static final String ANOTHER_PROTOCOL = "another";
  private static final String ANOTHER_HOST = "another";
  private static final int ANOTHER_PORT = 2;
  private static final String ANOTHER_PATH = "another";
  private static final URLStreamHandler ANOTHER_HANDLER = new TestURLStreamHandler("another");

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

  private static final URLStreamHandler CUSTOM_HANDLER = new TestURLStreamHandler("custom");

  @Test
  public void hashCodeBasedOnHost() {
    ArtifactRepository repository = new ArtifactRepository(
        ANY_PROTOCOL,
        SOME_HOST,
        ANY_PORT,
        ANY_PATH,
        ANY_HANDLER);

    int hashCode = repository.hashCode();

    assertThat(hashCode).isEqualTo(SOME_HOST_BASED_HASH_CODE);
  }

  @Test
  public void equalByObjectReference() {
    ArtifactRepository repository = new ArtifactRepository(
        ANY_PROTOCOL,
        ANY_HOST,
        ANY_PORT,
        ANY_PATH,
        ANY_HANDLER);
    ArtifactRepository anotherRepository = repository;

    boolean equals = repository.equals(anotherRepository);

    assertThat(equals).isTrue();
  }

  @Test
  public void equalByObjectFields() {
    ArtifactRepository repository = new ArtifactRepository(
        SOME_PROTOCOL,
        SOME_HOST,
        SOME_PORT,
        SOME_PATH,
        SOME_HANDLER);
    ArtifactRepository anotherRepository = new ArtifactRepository(
        SOME_PROTOCOL,
        SOME_HOST,
        SOME_PORT,
        SOME_PATH,
        SOME_HANDLER);

    boolean equals = repository.equals(anotherRepository);

    assertThat(equals).isTrue();
  }

  @Test
  public void notEqualByObjectType() {
    ArtifactRepository repository = new ArtifactRepository(
        ANY_PROTOCOL,
        ANY_HOST,
        ANY_PORT,
        ANY_PATH,
        ANY_HANDLER);
    Object anotherObject = new Object();

    boolean equals = repository.equals(anotherObject);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualByProtocol() {
    ArtifactRepository repository = new ArtifactRepository(
        SOME_PROTOCOL,
        SOME_HOST,
        SOME_PORT,
        SOME_PATH,
        SOME_HANDLER);
    ArtifactRepository anotherRepository = new ArtifactRepository(
        ANOTHER_PROTOCOL,
        SOME_HOST,
        SOME_PORT,
        SOME_PATH,
        SOME_HANDLER);

    boolean equals = repository.equals(anotherRepository);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualByHost() {
    ArtifactRepository repository = new ArtifactRepository(
        SOME_PROTOCOL,
        SOME_HOST,
        SOME_PORT,
        SOME_PATH,
        SOME_HANDLER);
    ArtifactRepository anotherRepository = new ArtifactRepository(
        SOME_PROTOCOL,
        ANOTHER_HOST,
        SOME_PORT,
        SOME_PATH,
        SOME_HANDLER);

    boolean equals = repository.equals(anotherRepository);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualByPort() {
    ArtifactRepository repository = new ArtifactRepository(
        SOME_PROTOCOL,
        SOME_HOST,
        SOME_PORT,
        SOME_PATH,
        SOME_HANDLER);
    ArtifactRepository anotherRepository = new ArtifactRepository(
        SOME_PROTOCOL,
        SOME_HOST,
        ANOTHER_PORT,
        SOME_PATH,
        SOME_HANDLER);

    boolean equals = repository.equals(anotherRepository);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualByPath() {
    ArtifactRepository repository = new ArtifactRepository(
        SOME_PROTOCOL,
        SOME_HOST,
        SOME_PORT,
        SOME_PATH,
        SOME_HANDLER);
    ArtifactRepository anotherRepository = new ArtifactRepository(
        SOME_PROTOCOL,
        SOME_HOST,
        SOME_PORT,
        ANOTHER_PATH,
        SOME_HANDLER);

    boolean equals = repository.equals(anotherRepository);

    assertThat(equals).isFalse();
  }

  @Test
  public void notEqualByHandler() {
    ArtifactRepository repository = new ArtifactRepository(
        SOME_PROTOCOL,
        SOME_HOST,
        SOME_PORT,
        SOME_PATH,
        SOME_HANDLER);
    ArtifactRepository anotherRepository = new ArtifactRepository(
        SOME_PROTOCOL,
        SOME_HOST,
        SOME_PORT,
        SOME_PATH,
        ANOTHER_HANDLER);

    boolean equals = repository.equals(anotherRepository);

    assertThat(equals).isFalse();
  }

  @Test
  @Tag("integration")
  public void getExpectedScopeDependencies(@TempDir File someRepositoryDir) throws Exception {
    ArtifactRepository repository = new ArtifactRepository(
        TEST_PROTOCOL,
        TEST_HOST,
        TEST_PORT,
        someRepositoryDir.getAbsolutePath(),
        TEST_HANDLER);
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
    ArtifactRepository repository = new ArtifactRepository(
        TEST_PROTOCOL,
        TEST_HOST,
        TEST_PORT,
        someRepositoryDir.getAbsolutePath(),
        TEST_HANDLER);
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
  public void getUsingCustomHandler(@TempDir File someRepositoryDir) throws Exception {
    ArtifactRepository repository = new ArtifactRepository(
        TEST_PROTOCOL,
        TEST_HOST,
        TEST_PORT,
        someRepositoryDir.getAbsolutePath(),
        CUSTOM_HANDLER);

    Throwable e = catchThrowable(() -> repository.get(ANY_ARTIFACT_COORDINATES));

    assertThat(e).isInstanceOf(UsingTestURLStreamHandlerException.class);
  }

  @Test
  @Tag("integration")
  public void failWhenDependencyVersionIsNotKnown(@TempDir File someRepositoryDir)
      throws Exception {
    ArtifactRepository repository = new ArtifactRepository(
        TEST_PROTOCOL,
        TEST_HOST,
        TEST_PORT,
        someRepositoryDir.getAbsolutePath(),
        TEST_HANDLER);
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
    ArtifactRepository repository = new ArtifactRepository(
        TEST_PROTOCOL,
        TEST_HOST,
        TEST_PORT,
        someRepositoryDir.getAbsolutePath(),
        TEST_HANDLER);
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
