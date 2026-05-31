package com.udea.usermembershipservice.acceptance.support;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

  public ApiResponse postRegisterHome(String nameHome, String gmail) {
    return postJson("/registerHome", Map.of("name", nameHome, "gmail", gmail));
  }

  public ApiResponse postUpdateRole(String nameHome, String gmail, String newRol, String gmailAdmin) {
    String qs = "?nameHome=" + enc(nameHome)
        + "&gmail=" + enc(gmail)
        + "&newRol=" + enc(newRol)
        + "&gmailAdmin=" + enc(gmailAdmin);
    HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(url("/updateRole" + qs)))
        .PUT(HttpRequest.BodyPublishers.noBody())
        .build();
    return send(req);
  }

  public ApiResponse getHomeByName(String nameHome) {
    HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(url("/getHomeByName?name=" + enc(nameHome))))
        .GET()
        .build();
    return send(req);
  }

  public ApiResponse getMembersOfHome(String nameHome) {
    HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(url("/GetMemberHome?nameHome=" + enc(nameHome))))
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

  private String enc(String v) {
    return URLEncoder.encode(v == null ? "" : v, StandardCharsets.UTF_8);
  }
}
