package com.example.demo.modules.chat;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class CustomHandshakeService extends HandshakeWebSocketService {

  @Override
  public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {
    String user = exchange.getRequest().getQueryParams().getFirst("user");
    if(user==null||user.isBlank()) {
      exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
      return exchange.getResponse().writeWith(
        Mono.just(exchange.getResponse().bufferFactory().wrap("userId is required.".getBytes()))
      );
    }

    return super.handleRequest(exchange, handler);
  }
}
