package com.example.demo.modules.chat;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString
public class ChatMessage {
    private MessageType type;
    private Instant timestamp;
    private String text;
    private String sender;
    private String imgId;
}
