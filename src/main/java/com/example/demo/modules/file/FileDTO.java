package com.example.demo.modules.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class FileDTO {
    private final String filename;
    private final byte[] data;
}
