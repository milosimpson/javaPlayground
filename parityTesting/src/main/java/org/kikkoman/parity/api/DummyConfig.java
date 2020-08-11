package org.kikkoman.parity.api;

import java.util.HashMap;
import org.apache.kafka.common.config.ConfigDef;
import io.confluent.rest.RestConfig;
import java.util.Map;

public class DummyConfig extends RestConfig {

  private static ConfigDef config = baseConfigDef();

  public static Map<String,String>  getDefaultParams() {
    Map<String,String> params = new HashMap<>();
    params.put("authentication.method", "NONE");
    params.put("advertised.listeners", "localhost:8080");
    return params;
  }

  public DummyConfig() {
    super(config, getDefaultParams());
  }

  public DummyConfig(Map<?, ?> props) {
    super(config, props);
  }
}
