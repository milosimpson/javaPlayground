package org.kikkoman.parity.integration;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.kikkoman.parity.service.BackendResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/*
 * This class has more boilerplate, but it does give a better indication of when there is an error.
 *
 * @BeforeClass can get parameters from the suite definition, so have a @BeforeClass to
 *  capture them.
 *
 * Then use a @DataProvider so that part of the test signature tells us which backend and port
 *  are being used.
 */
public class ApiTestTwo {

  private final ObjectMapper om = new ObjectMapper();

  private final Client client = ClientBuilder.newClient();

  private String backendType;
  private String portStr;

  @BeforeClass
  @Parameters({ "backendType", "port" })
  public void setupAndCaptureParams(String backendType,  String portStr) {
    System.out.println("setupAndCaptureParams with port:" + portStr);
    this.backendType = backendType;
    this.portStr = portStr;
  }

  @DataProvider
  public Object[][] provideData() {
    return new Object[][]{
          { backendType, portStr }
    };
  }

  @Test(dataProvider = "provideData")
  // These test params are visible in test reports and on the build console with
  //   org.kikkoman.parity.util.ConsoleListener class running/outputting to console
  public void verifyCallTwo(String backendType,  String portStr) throws Exception {

    System.out.println();
    System.out.println("ApiTestTwo.verifyCall configed with port:" + portStr);
    System.out.println();

    String hostAndPort = "http://localhost:" + portStr;

    Response response = client
          .target(hostAndPort)
          .path("dummy/callTwo")
          .request(MediaType.APPLICATION_JSON)
          .get();

    assertEquals(response.getStatus(), 200);

    String responseStr = response.readEntity(String.class);
    BackendResult backendResult = om.readValue(responseStr,
          new TypeReference<BackendResult>() {});

    assertTrue(backendResult.result());

    System.out.println("ApiTestTwo for url:"+hostAndPort + " saw backend:" + backendResult.getBackendName());
  }
}
