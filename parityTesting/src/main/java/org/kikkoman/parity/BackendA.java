package org.kikkoman.parity;

public class BackendA implements Backend {

  public String backendCall() {

    try {
      Thread.sleep(1000);
    }
    catch (Exception e) {}

    return "BACKEND AAAA";
  }
}
