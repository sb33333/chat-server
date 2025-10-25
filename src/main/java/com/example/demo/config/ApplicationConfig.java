package com.example.demo.config;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.modules.file.BlobFile;
import com.example.demo.modules.file.BlobFileRepository;
import com.example.demo.modules.file.BlobFileService;
import com.example.demo.modules.file.Cryptor;
import com.example.demo.modules.file.FileRepository;
import com.example.demo.modules.file.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class ApplicationConfig {
    
    private final FileRepository fileRepository;
    private final BlobFileRepository blobFileRepository;
    private SecureRandom random = new SecureRandom();
    
    @Bean
    public ObjectMapper objectMapper() {
        var om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om;
    }
    
    @Bean
    public Cryptor cryptor () {
        var bytes = new byte[32];
        random.nextBytes(bytes);
        return new Cryptor(bytes);
    }
    @Bean
    public FileService fileService () {
        return new FileService(fileRepository, cryptor(), "/upload");
    }
    @Bean
    public BlobFileService blobFileService() {
        return new BlobFileService(blobFileRepository, cryptor());
    }
}
