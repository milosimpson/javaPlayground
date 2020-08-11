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
import org.testng.annotations.Test;

public class ApiTest {

  private final Client client = ClientBuilder.newClient();

  private static final ObjectMapper om = new ObjectMapper();

  @Test
  public void verifyCall() throws Exception {

    Response response = client
          .target("http://localhost:8080")
          .path("dummy/callOne")
          .request(MediaType.APPLICATION_JSON)
          .get();

    assertEquals(response.getStatus(), 200);

    String responseStr = response.readEntity(String.class);
    BackendResult backendResult = om.readValue(responseStr,
          new TypeReference<BackendResult>() {});

    assertTrue(backendResult.getBackendName().contains("AAAA"));

    assertEquals(response.getStatus(), 200);
  }
}
