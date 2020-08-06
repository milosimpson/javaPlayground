package org.kikkoman.parity.api;

import org.apache.kafka.common.config.ConfigDef;
import io.confluent.rest.RestConfig;
import java.util.Map;

public class DummyConfig extends RestConfig {

  private static ConfigDef config = baseConfigDef();

  public DummyConfig() {
    super(config);
  }

  public DummyConfig(Map<?, ?> props) {
    super(config, props);
  }
}
