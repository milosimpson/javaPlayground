package org.kikkoman.parity.integration;

import static org.kikkoman.parity.api.DummyConfig.getParams;

import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.kikkoman.parity.api.DummyApplication;
import org.kikkoman.parity.api.DummyConfig;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/*
 * This class does not need to annotated with @Test
 */
public class ApiParitySuite {

  private DummyApplication app;

  @BeforeSuite
  @Parameters({ "backendType", "port" })
  public void suiteSetup(@Optional("A")  String backendType, @Optional("8080") String port) throws Exception {

    System.out.println();
    System.out.println("ApiParitySuite backend:" + backendType + " config-ed with port:" + port);
    System.out.println();

    // pass backend type and port values from TestNG suite def into the config of the Api
    DummyConfig config = new DummyConfig(getParams(backendType, Integer.valueOf(port)));
    app = new DummyApplication(config);
    Server server = app.createServer();
    server.start();

    int actualPort = getPrimaryPort(server);
    System.out.println();
    System.out.println("Actual server port is:" + actualPort);
    System.out.println();

  }

  public int getPrimaryPort(Server server) {
    return ((NetworkTrafficServerConnector) server.getConnectors()[0]).getLocalPort();
  }

  @AfterSuite
  public void suiteTeardown()  throws Exception {
    app.stop();
  }
}
