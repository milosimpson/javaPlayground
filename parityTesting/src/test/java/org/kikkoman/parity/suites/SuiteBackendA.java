package org.kikkoman.parity.suites;

import static org.kikkoman.parity.api.DummyConfig.getParams;

import org.kikkoman.parity.api.DummyApplication;
import org.kikkoman.parity.api.DummyConfig;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/*
 * This class does not need to annotated with @Test
 */
public class SuiteBackendA {

  private DummyApplication app;

  @BeforeSuite
  public void suiteSetup() throws Exception {
    DummyConfig config = new DummyConfig(getParams("A"));
    app = new DummyApplication(config);
    app.start();
  }

  @AfterSuite
  public void suiteTeardown()  throws Exception {
    app.stop();
  }
}
