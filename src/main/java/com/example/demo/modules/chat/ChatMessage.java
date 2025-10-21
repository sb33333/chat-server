package com.example.demo.modules.chat;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ChatMessage {

    private Instant timestamp;
    private String message;
    private String sender;
}
