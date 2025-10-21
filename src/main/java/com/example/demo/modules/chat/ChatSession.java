package com.example.demo.modules.chat;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class ChatSession {

    
    private Sinks.Many<ChatMessage> sink;
    private Flux<ChatMessage> messageFlux;

    public ChatSession() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
        this.messageFlux = sink.asFlux().replay(0).autoConnect();
    }

    public void publish(ChatMessage msg) {
        sink.tryEmitNext(msg);
    }

    public Flux<ChatMessage> getFlux() {
        return this.messageFlux;
    }

    public void close() {
        this.sink.tryEmitComplete();
        this.sink = null;
        this.messageFlux = null;
    }
}
