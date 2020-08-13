package org.kikkoman.parity.unit;


import static org.testng.Assert.assertTrue;

import org.kikkoman.parity.service.Backend;
import org.kikkoman.parity.service.BackendA;
import org.kikkoman.parity.service.BackendB;
import org.kikkoman.parity.service.BackendResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class SimpleBackendUnitTest {

  private Backend backend;

  @BeforeClass
  // This "Parameter" gets injected from the test suite config xml file
  @Parameters({ "backendType" })
  // the Optional / default value, allows developers to run this test by itself / from IDE
  //
  // If developer wants to set the other backend type, they can quickly hack it here
  //  or in Intellij edit the "Configuration" -> "Parameters" of the test adding
  //  "backendType" : "B"
  //
  // Less hassle to just change it here temporarily, and if that change gets checked in, doesn't
  //  really matter as test suite will still run with both.
  public void setup(@Optional("A") String backendType) {

    if ("A".equals(backendType)) {
      backend = new BackendA();
    }
    else if ("B".equals(backendType)) {
      backend = new BackendB();
    }
    else {
      throw new RuntimeException("Unknown Backend specified:" + backendType);
    }
  }

  @Test
  public void verifyCallOne() {
    BackendResult result = backend.callOne();
    assertTrue( result.getBackendName().contains("BACKEND") );
    System.out.println("Backend was: " + result.getBackendName());
  }

  @Test
  public void verifyCallTwo() {
    BackendResult result = backend.callTwo();
    assertTrue( result.getBackendName().contains("BACKEND") );
    System.out.println("Backend was: " + result.getBackendName());
  }
}
