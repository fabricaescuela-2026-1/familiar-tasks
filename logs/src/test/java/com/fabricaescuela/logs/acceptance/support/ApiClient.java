package com.fabricaescuela.logs.acceptance.support;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApiClient {

  @Autowired
  private ApplicationContext context;

  private final HttpClient http = HttpClient.newHttpClient();
  private final ObjectMapper mapper = new ObjectMapper();

  private int port() {
    return ((WebServerApplicationContext) context).getWebServer().getPort();
  }

  private String url(String path) {
    return "http://localhost:" + port() + path;
  }

  public ApiResponse postCreateLog(String id, String idUser, String modifiedElement, String action) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("id", id);
    body.put("idUser", idUser);
    body.put("modifiedElement", modifiedElement);
    body.put("action", action);
    return postJson("/api/logs", body);
  }

  public ApiResponse getAllLogs() {
    HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(url("/api/logs")))
        .GET()
        .build();
    return send(req);
  }

  private ApiResponse postJson(String path, Map<String, Object> body) {
    try {
      String json = mapper.writeValueAsString(body);
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(url(path)))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
          .build();
      return send(req);
    } catch (Exception e) {
      throw new IllegalStateException("Error serializando JSON", e);
    }
  }

  private ApiResponse send(HttpRequest req) {
    try {
      HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      return new ApiResponse(resp.statusCode(), resp.body(), mapper);
    } catch (Exception e) {
      throw new IllegalStateException("Error ejecutando petición HTTP", e);
    }
  }
}
