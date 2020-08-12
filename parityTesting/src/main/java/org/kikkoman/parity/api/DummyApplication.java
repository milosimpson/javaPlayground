package org.kikkoman.parity.api;

import io.confluent.rest.Application;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.MediaType;
import org.kikkoman.parity.service.Backend;
import org.kikkoman.parity.service.BackendA;
import org.kikkoman.parity.service.BackendB;
import org.kikkoman.parity.service.BackendResult;

public class DummyApplication extends Application<DummyConfig> {

  private final Backend backend;

  public DummyApplication(DummyConfig config) {
    super(config);

    String backendType = config.getString("backend");
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

  @Override
  public void setupResources(Configurable<?> config, DummyConfig appConfig) {
    config.register(new DummyResource(backend));
  }


  @Path("/dummy")
  public static class DummyResource {

    private final Backend backend;

    public DummyResource(Backend backend) {
      this.backend = backend;
    }

    @GET
    @Path("callOne")
    @Produces(MediaType.APPLICATION_JSON)
    public BackendResult call1() {
      return backend.callOne();
    }

    @GET
    @Path("callTwo")
    @Produces(MediaType.APPLICATION_JSON)
    public BackendResult call2() {
      return backend.callTwo();
    }
  }
}
