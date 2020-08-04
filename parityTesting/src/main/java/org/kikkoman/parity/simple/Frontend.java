package org.kikkoman.parity.simple;

public class Frontend {

  private final Backend backend;

  public Frontend(Backend backend) {
    this.backend = backend;
  }

  public String frontendCall() {
    return backend.backendCall();
  }
}
