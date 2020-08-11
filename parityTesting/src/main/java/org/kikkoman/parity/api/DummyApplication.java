package org.kikkoman.parity.api;

import io.confluent.rest.Application;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.MediaType;
import org.kikkoman.parity.Utils;
import org.kikkoman.parity.service.BackendResult;

public class DummyApplication extends Application<DummyConfig> {

  public DummyApplication(DummyConfig config) {
    super(config);


  }

  @Override
  public void setupResources(Configurable<?> config, DummyConfig appConfig) {
    config.register(new DummyResource());
  }


  @Path("/dummy")
  public static class DummyResource {

    @GET
    @Path("callOne")
    @Produces(MediaType.APPLICATION_JSON)
    public BackendResult call1() {
      // simulate work
      Utils.sleep();

      return new BackendResult("AAAA");
    }

    @GET
    @Path("callTwo")
    @Produces(MediaType.APPLICATION_JSON)
    public BackendResult call2() {
      // simulate work
      Utils.sleep();

      return new BackendResult("AAAA");
    }
  }
}
