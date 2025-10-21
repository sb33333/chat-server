package com.example.demo.modules.chat;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ObjectMapper objectMapper;
    
    // 단일 채팅방용 Sink
    private final Sinks.Many<ChatMessage> sink = Sinks.many().multicast().onBackpressureBuffer();
    

    // 외부에서 구독 가능한 Flux
    public Flux<ChatMessage> messageFlux() {
        return sink.asFlux();
    }

    // 클라이언트로부터 들어온 메시지 처리
    public Mono<ChatMessage> processIncoming(String payload) {
        return Mono.fromCallable(() -> objectMapper.readValue(payload, ChatMessage.class))
            .doOnNext(msg -> msg.setTimestamp(Instant.now()))
            .onErrorResume(e -> {
                log.info("invalid message ignored: {}", payload);
                return Mono.empty();
            })
            ;
    }

    // 서버에서 메시지 발행
    public void broadcast(ChatMessage message) {
        sink.tryEmitNext(message);
    }

    // JSON 변환(응답용)
    public String toJson(ChatMessage msg) {
        try {
            return objectMapper.writeValueAsString(msg);
        } catch (Exception e) {
            return "{}";
        }
    }
}
