package org.kikkoman.parity.fancy;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.kikkoman.parity.integration.ApiParitySuite;
import org.kikkoman.parity.service.BackendResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/*
 * Fancy Test that can determine if it being run as part of a full test suite run or from
 *  a developer running from Intellij.
 *
 * Trick is to have a sentinel "paramSource" and if needed start the Api.
 *
 * While neat, it is a bunch of boilerplate to have a test.
 * Might be able to move it to a base class.
 */
public class ApiTestFancy {

  private final ObjectMapper om = new ObjectMapper();

  private final Client client = ClientBuilder.newClient();

  private String backendType;
  private String portStr;
  private ApiParitySuite apiParitySuite = null;


  @BeforeClass
  @Parameters({ "paramSource", "backendType", "port" })
  public void setupAndCaptureParams(@Optional("testClass") String paramSource, // sentinel value
                                    @Optional("A") String backendType,  // Developer can edit
                                    @Optional("8080") String portStr)   // Developer can edit
        throws Exception {
    System.out.println("setupAndCaptureParams with port:" + portStr);
    this.backendType = backendType;
    this.portStr = portStr;

    // if this test is being run as part of a suite, paramSource would not be "testClass"
    if("testClass".equals(paramSource)) {
      // we need to do the work of SuiteSetup and Teardown
      apiParitySuite = new ApiParitySuite();
      apiParitySuite.suiteSetup(backendType, portStr);
    }
  }

  @AfterClass
  public void maybeShutdown() throws Exception {
    if (apiParitySuite != null) {
      apiParitySuite.suiteTeardown();
    }
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
    System.out.println("ApiTestFancy.verifyCall configed with port:" + portStr);
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

    System.out.println("ApiTestFancy for url:"+hostAndPort + " saw backend:" + backendResult.getBackendName());
  }
}
