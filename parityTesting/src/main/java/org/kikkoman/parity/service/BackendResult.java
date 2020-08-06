package org.kikkoman.parity.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BackendResult {

  boolean result;
  private String backendName;

  public BackendResult(String backendName) {
    this(backendName, true);
  }

  public BackendResult(String backendName, boolean result) {
    this.backendName = backendName;
    this.result = result;
  }

  @JsonProperty
  public String getBackendName() {
    return backendName;
  }

  @JsonProperty
  public boolean result() {
    return result;
  }
}
