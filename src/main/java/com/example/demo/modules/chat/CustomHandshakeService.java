


package com.example.demo.modules.chat;

public class CustomHandshakeService extends HandshakeWebSocketService {

  @Override
  public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {
    String user = exchange.getRequest().getQueryParams().getFirst("user");
    if(user==null||user.isBlank()) {
      exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
      return exchange.getResponse().writeWith(
        Mono.just(exchange.getResponse().bufferFactory().wrap(JsonUtil.message("userId is required.", StandardCharsets.UTF_8))
      );
    }

    return super.handleRequest(exchange, handler);
  }
}
