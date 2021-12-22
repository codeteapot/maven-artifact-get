package com.github.codeteapot.tools.artifact;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static javax.xml.bind.JAXBContext.newInstance;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Stream;
import javax.xml.bind.JAXBException;

/**
 * Artifact repository.
 */
public class ArtifactRepository {

  private static final Map<String, String> EXTENSION_MAP = Stream.of(
      new SimpleEntry<>("jar", "jar"),
      new SimpleEntry<>("ejb", "jar"),
      new SimpleEntry<>("ejb-client", "jar"),
      new SimpleEntry<>("war", "war"),
      new SimpleEntry<>("ear", "ear"),
      new SimpleEntry<>("pom", "pom"))
      .collect(toMap(Entry::getKey, Entry::getValue));

  private static final String DEFAULT_EXTENSION = "jar";

  private static final String DEFAULT_PROTOCOL = "file";
  private static final String DEFAULT_HOST = "";
  private static final int DEFAULT_PORT = -1;

  private final String protocol;
  private final String host;
  private final int port;
  private final String path;
  private final URLStreamHandler handler;

  /**
   * With the given parameters to create the underlying {@link URL}.
   *
   * @param protocol Protocol.
   * @param host Host.
   * @param port Port. Can be {@code null} for default.
   * @param path Path.
   * @param handler Handler. Can be {@code null} for default.
   */
  public ArtifactRepository(
      String protocol,
      String host,
      Integer port,
      String path,
      URLStreamHandler handler) {
    this.protocol = ofNullable(protocol).orElse(DEFAULT_PROTOCOL);
    this.host = ofNullable(host).orElse(DEFAULT_HOST);
    this.port = ofNullable(port).orElse(DEFAULT_PORT);
    this.path = requireNonNull(path);
    this.handler = handler;
  }

  /**
   * Get an artifact through this repository.
   *
   * @param coordinates Artifact coordinates.
   *
   * @return The artifact.
   *
   * @throws ArtifactRepositoryException When some repository error has been occurred.
   * @throws IOException When an I/O error has been occurred.
   */
  public Artifact get(ArtifactCoordinates coordinates)
      throws ArtifactRepositoryException, IOException {
    try (InputStream input = file(coordinates.getPath("pom")).openStream()) {
      XMLProject project = (XMLProject) newInstance(XMLProject.class)
          .createUnmarshaller()
          .unmarshal(input);
      return new Artifact(
          file(coordinates.getPath(project.getExtension(this::fromPackaging))),
          project.getDependencies());
    } catch (JAXBException | MalformedURLException e) {
      throw new ArtifactRepositoryException(e);
    } catch (UncheckedArtifactRepositoryException e) {
      throw e.getCause();
    }
  }

  /**
   * Hash based on {@code host}.
   */
  @Override
  public int hashCode() {
    return host.hashCode();
  }

  /**
   * Equality based on {@code protocol}, {@code host}, {@code port}, {@code path} and
   * {@code handler}.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof ArtifactRepository) {
      ArtifactRepository repository = (ArtifactRepository) obj;
      return protocol.equals(repository.protocol)
          && host.equals(repository.host)
          && port == repository.port
          && path.equals(repository.path)
          && Objects.equals(handler, repository.handler);
    }
    return false;
  }

  private String fromPackaging(String packaging) {
    return ofNullable(packaging).map(EXTENSION_MAP::get).orElse(DEFAULT_EXTENSION);
  }

  private URL file(String relativePath) throws MalformedURLException {
    return handler == null
        ? new URL(protocol, host, port, absoluteFile(relativePath))
        : new URL(protocol, host, port, absoluteFile(relativePath), handler);
  }

  private String absoluteFile(String relativePath) {
    return path.concat(relativePath);
  }
}
