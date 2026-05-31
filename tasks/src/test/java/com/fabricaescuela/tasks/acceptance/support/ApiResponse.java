package com.fabricaescuela.tasks.acceptance.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiResponse {

  private final int statusCode;
  private final String body;
  private final ObjectMapper mapper;

  public ApiResponse(int statusCode, String body, ObjectMapper mapper) {
    this.statusCode = statusCode;
    this.body = body == null ? "" : body;
    this.mapper = mapper;
  }

  public int statusCode() {
    return statusCode;
  }

  public String body() {
    return body;
  }

  public String bodyLower() {
    return body.toLowerCase();
  }

  public String jsonString(String field) {
    try {
      JsonNode root = mapper.readTree(body);
      JsonNode node = root.get(field);
      return node == null || node.isNull() ? null : node.asText();
    } catch (Exception e) {
      return null;
    }
  }
}
