package com.example.demo.modules.chat;

import java.time.Instant;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class SessionManager {

    private ChatSession chatSession = null;
    public void openSession() {
        chatSession = new ChatSession();
        log.info("session created:::{}", chatSession.toString());
    }

    public void closeSession() {
        if (chatSession == null) return;
        chatSession.close();
        chatSession = null;
    }

    public boolean isOpen() {
        return !(chatSession == null);
    }

    public void broadcast (ChatMessage msg) {
        if (!isOpen()) {
            log.error("chatSession is not opened");
            return;
        }
        msg.setTimestamp(Instant.now());
        chatSession.publish(msg);
    }

    public void broadcast (String text, String sender) {
        var chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setMessage(text);
        this.broadcast(chatMessage);
    }

    public Flux<ChatMessage> getFlux () {
        if (!isOpen()) {
            log.error("chatSession is not opened");
            return null;
        }
        return chatSession.getFlux();
    }

}
