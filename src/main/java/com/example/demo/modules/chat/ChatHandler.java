package com.example.demo.modules.chat;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ChatHandler {

    private final SessionManager sessionManager;

    public Mono<ServerResponse> checkSession (ServerRequest request) {
        return ServerResponse.ok().bodyValue(sessionManager.isOpen());
    }
}
