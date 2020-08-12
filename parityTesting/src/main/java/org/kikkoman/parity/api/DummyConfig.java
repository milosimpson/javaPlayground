package org.kikkoman.parity.api;

import java.util.HashMap;
import org.apache.kafka.common.config.ConfigDef;
import io.confluent.rest.RestConfig;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

public class DummyConfig extends RestConfig {

//  public ConfigDef define(String name, Type type, Object defaultValue, Importance importance, String documentation) {

    private static final ConfigDef config = baseConfigDef()
        .define("backend", Type.STRING,"A", Importance.LOW, "Which backend to use A or B");

  public static Map<String,String>  getDefaultParams() {
    Map<String,String> params = new HashMap<>();
    params.put("authentication.method", "NONE");
    params.put("advertised.listeners", "localhost:8080");
    return params;
  }

  public static Map<String,String> getParams(String backend, int port) {
    Map<String,String> params = new HashMap<>();
    params.put("authentication.method", "NONE");
    params.put("listeners", "http://localhost:" + port);
    params.put("advertised.listeners", "http://localhost:" + port);
    params.put("backend", backend);
    return params;
  }

  public DummyConfig() {
    super(config, getDefaultParams());
  }

  public DummyConfig(Map<?, ?> props) {
    super(config, props);
  }
}
