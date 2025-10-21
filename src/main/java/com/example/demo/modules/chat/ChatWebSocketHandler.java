package com.example.demo.modules.chat;

import java.time.Instant;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler{

    //
    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // 클라이언트 -> 서버
        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(this::processIncoming)
                .doOnNext(sessionManager::broadcast)
                .then();

        // 서버 -> 클라이언트
        Mono<Void> output = session.send(
                sessionManager.getFlux()
                    .map(this::toJson)
                    .map(session::textMessage)
        );

        return Mono.zip(input, output).then();
    }

    // 클라이언트로부터 들어온 메시지 처리
    private Mono<ChatMessage> processIncoming(String payload) {
        return Mono.fromCallable(() -> objectMapper.readValue(payload, ChatMessage.class))
            .doOnNext(msg -> msg.setTimestamp(Instant.now()))
            .onErrorResume(e -> {
                log.info("invalid message ignored: {}", payload);
                return Mono.empty();
            })
            ;
    }

    // JSON 변환(응답용)
    private String toJson(ChatMessage msg) {
        try {
            return objectMapper.writeValueAsString(msg);
        } catch (Exception e) {
            return "{}";
        }
    }
}
