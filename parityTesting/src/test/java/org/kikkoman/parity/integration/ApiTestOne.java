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
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/*
 * So this class works, but a downside is that when there is an error, you can easily tell
 *  which backend and port was being used.
 */
public class ApiTestOne {

  private final ObjectMapper om = new ObjectMapper();

  private final Client client = ClientBuilder.newClient();

  private String hostAndPort;

  @BeforeClass
  @Parameters({ "port" })
  public void setup(@Optional("8080") String portStr) {
    System.out.println();
    System.out.println("ApiTestOne configed with port:" + portStr);
    System.out.println();
    hostAndPort = "http://localhost:" + portStr;
  }

  @Test
  public void verifyCallOne() throws Exception {

    Response response = client
          .target(hostAndPort)
          .path("dummy/callOne")
          .request(MediaType.APPLICATION_JSON)
          .get();

    assertEquals(response.getStatus(), 200);

    String responseStr = response.readEntity(String.class);
    BackendResult backendResult = om.readValue(responseStr,
          new TypeReference<BackendResult>() {});

    assertTrue(backendResult.result());

    System.out.println("ApiTestOne for url:"+hostAndPort + " saw backend:" + backendResult.getBackendName());
  }
}
