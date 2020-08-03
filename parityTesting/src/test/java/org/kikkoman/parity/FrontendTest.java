package org.kikkoman.parity;


import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FrontendTest {

  private Frontend frontend;

  @BeforeClass
  public void setup() {
    Backend backend = new BackendA();
    frontend = new Frontend(backend);
  }

  @Test
  public void verifyCall() {
    final String result = frontend.frontendCall();
    assertTrue( result.startsWith("BACKEND") );
    System.out.println("Call result was : " + result);
  }
}
