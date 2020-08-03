package org.kikkoman.parity;


import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class FrontendTest {

  private Frontend frontend;

  @Parameters({ "backendType" })
  @BeforeClass
  public void setup(@Optional("A") String backendType) {
    Backend backend;

    if ("A".equals(backendType)) {
      backend = new BackendA();
    }
    else if ("B".equals(backendType)) {
      backend = new BackendB();
    }
    else {
      throw new RuntimeException("Unknown Backend specified:" + backendType);
    }

    frontend = new Frontend(backend);
  }

  @Test
  public void verifyCall() {
    final String result = frontend.frontendCall();
    assertTrue( result.startsWith("BACKEND") );
    System.out.println("Call result was : " + result);
  }
}
