package org.kikkoman.parity.service;

import org.kikkoman.parity.Utils;

// Backend B is quick to startup, but slower to service requests
public class BackendB implements Backend {

  public BackendB() {
    Utils.sleep(500);
  }

  public BackendResult callOne() {

    // simulate work
    Utils.sleep(2500);

    return new BackendResult("BACKEND BBBB");
  }

  public BackendResult callTwo() {

    // simulate work
    Utils.sleep(1500);

    return new BackendResult("BACKEND BBBB");
  }
}
