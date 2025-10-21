package com.example.demo.modules.manager;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.demo.modules.chat.SessionManager;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ManagerHandler {

    private final SessionManager sessionManager;

    public Mono<ServerResponse> createSession (ServerRequest request) {
        if (sessionManager.isOpen()) return ServerResponse.badRequest().bodyValue("session exists");
        sessionManager.openSession();
        return ServerResponse.ok().bodyValue("session opened.");
    }

    public Mono<ServerResponse> closeSession(ServerRequest request) {
        if (sessionManager.isOpen()) {
            sessionManager.closeSession();
            return ServerResponse.ok().bodyValue("session closed.");
        }
        return ServerResponse.badRequest().bodyValue("session is not opened.");
    }

}
