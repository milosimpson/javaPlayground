package org.kikkoman.parity.service;

import org.kikkoman.parity.Utils;

// Backend A is slower to startup, but quicker to service calls
public class BackendA implements Backend {

  public BackendA() {
    Utils.sleep(2000);
  }

  public BackendResult callOne() {

    // simulate work
    Utils.sleep(700);

    return new BackendResult("BACKEND AAAA");
  }

  public BackendResult callTwo() {

    // simulate work
    Utils.sleep(1000);

    return new BackendResult("BACKEND AAAA");
  }
}
