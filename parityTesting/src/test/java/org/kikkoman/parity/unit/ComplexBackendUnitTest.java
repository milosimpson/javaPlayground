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

// Simulate making a series of backened calls
public class ComplexBackendUnitTest {

  private Backend backend;

  @BeforeClass
  // This gets injected from the test suite config xml file
  @Parameters({ "backendType" })
  // the Optional / default value, allows developers to run this test by itself / from IDE
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
  public void verifySeriesOfCalls() {

    // Imagine a create, read, delete test
    {
      BackendResult result = backend.callOne();
      assertTrue(result.getBackendName().contains("BACKEND"));
      System.out.println("Backend was: " + result.getBackendName());
    }

    {
      BackendResult result = backend.callTwo();
      assertTrue(result.getBackendName().contains("BACKEND"));
    }

    {
      BackendResult result = backend.callOne();
      assertTrue( result.getBackendName().contains("BACKEND") );
    }
  }
}
