package org.kikkoman.parity.api;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.kikkoman.parity.service.BackendResult;
import org.testng.annotations.Test;

public class ApiTestCall1 {

  private final Client client = ClientBuilder.newClient();

  @Test
  public void verifyCall() {

    Response response = client
          .target("http://localhost:8090")
          .path("dummy/call1")
          .request(MediaType.APPLICATION_JSON)
          .get();

    BackendResult backendResult = response.readEntity(BackendResult.class);
    assertTrue(backendResult.getBackendName().contains("AAAA"));

    assertEquals(response.getStatus(), 200);
  }
}
