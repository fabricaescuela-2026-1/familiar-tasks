package com.fabrica.authentication.acceptance.support;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TestContext {

  private ApiResponse lastResponse;
  private final Map<String, Object> bag = new HashMap<>();

  public void reset() {
    this.lastResponse = null;
    this.bag.clear();
  }

  public ApiResponse getLastResponse() {
    return lastResponse;
  }

  public void setLastResponse(ApiResponse response) {
    this.lastResponse = response;
  }

  public void put(String key, Object value) {
    bag.put(key, value);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return (T) bag.get(key);
  }
}
