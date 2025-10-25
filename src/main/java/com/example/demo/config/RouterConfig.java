
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.AbstractHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.server.ServerWebExchange;

import com.example.demo.modules.chat.ChatHandler;
import com.example.demo.modules.chat.ChatWebSocketHandler;
import com.example.demo.modules.chat.CustomHandshakeService;
import com.example.demo.modules.file.FileHandler;
import com.example.demo.modules.manager.ManagerHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RouterConfig {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ManagerHandler managerHandler;
    private final ChatHandler chatHandler;
    private final FileHandler fileHandler;

    // @Bean
    // public HandlerMapping handlerMapping() {
    //     Map<String, WebSocketHandler> map = new HashMap<>();
    //     map.put("/ws/chat", chatWebSocketHandler);
    //     return new SimpleUrlHandlerMapping(map, 10);
    // }
    @Bean
    public WebSocketHandlerAdapter handerAdapter() {
        var customHandshakeService = new CustomHandshakeService() ;
        return new WebSocketHandlerAdapter(customHandshakeService);
    }

    @Bean
    public RouterFunction<ServerResponse> route () {
        return RouterFunctions
        .route(RequestPredicates.POST("/manage/chat"), managerHandler::createSession)
        .andRoute(RequestPredicates.DELETE("/manage/chat"), managerHandler::closeSession)
        ;
        // RequestPredicate.POST("/manage/chat"), managerHandler:::createSession
    }




    @Bean
    public HandlerMapping chatHandlerMapping () {
        var routerFunction = RouterFunctions.route(RequestPredicates.GET("/chat"), chatHandler::checkSession);

        var mapping = new AbstractHandlerMapping() {

            @Override
            protected Mono<?> getHandlerInternal(ServerWebExchange exchange) {
                var request = exchange.getRequest();
                var path = request.getURI().getPath();

                if (!"/chat".equals(path)) return Mono.empty();

                String upgrade = exchange.getRequest().getHeaders().getUpgrade();
                log.info("upgrade:::{}",upgrade);
                // WebSocket handshake 요청인가?

                if ("websocket".equalsIgnoreCase(upgrade)) {
                    // WebSocket 요청이면 이 핸들러로 연결
                    return Mono.just(chatWebSocketHandler);
                } else {
                    return Mono.just(RouterFunctions.toHttpHandler(routerFunction));
                }
            }

        };
        mapping.setOrder(-1);
        return mapping;
    }

    @Bean
    public RouterFunction<ServerResponse> fileRouting () {
        return RouterFunctions
        .route()
        .POST("/file", RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), fileHandler::upload)
        .GET("/file/{id}", fileHandler::download)
        .POST("/file_b", RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), fileHandler::upload_blob)
        .GET("/file_b", fileHandler::download_blob)
        .build()
        ;
    }


}
