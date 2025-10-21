package com.example.demo.modules.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
public class FileService{
    private final FileRepository fileRepository;
    private final String UPLOAD_DIR;
    private final byte[] SECRET_KEY;
    private final String CIPHER_INSTANCE_NAME = "AES/GCM/NoPadding";

    private SecretKey getSecretKey() {
        return new SecretKeySpec(SECRET_KEY, "AES");
    }

    private byte[] encrypt(byte[] data) {
        try {

            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_NAME);
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), spec);
            byte[] encrypted = cipher.doFinal(data);

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
            buffer.put(iv);
            buffer.put(encrypted);
            return buffer.array();

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed.", e);
        }
    }

    private byte[] decrypt (byte[] encryptedWithIv) {
        try {

            ByteBuffer buffer = ByteBuffer.wrap(encryptedWithIv);
            byte[] iv = new byte[12];
            buffer.get(iv);
            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_NAME);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new GCMParameterSpec(128, iv));
            return cipher.doFinal(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Decryption failed.", e);
        }

    } // end of decrypt()

    public Mono<FileMetadata> saveFile(String filename, Mono<byte[]> fileBytesMono) {
        return fileBytesMono
        .map(this::encrypt)
        .flatMap(encrypted ->
        Mono.fromCallable(() -> {
            try {
                Path dir = Paths.get(UPLOAD_DIR);
                Files.createDirectories(dir);
                String uuid = UUID.randomUUID().toString();
                Path path = dir.resolve(uuid + "_" + filename);
                Files.write(path, encrypted);
                FileMetadata metadata = new FileMetadata();
                metadata.setFileId(uuid);
                metadata.setFilename(filename);
                metadata.setPath(path.toString());
                metadata.setUploadDate(new Date());
                log.info("{}:::saved", uuid);
                return fileRepository.save(metadata);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        })
        )
        .subscribeOn(Schedulers.boundedElastic());
    } // end of saveFile()

    public Mono<FileDTO> loadFile (String id) {
        return Mono.fromCallable(() -> {
            FileMetadata metadata = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("file not found."));
            byte[] encrypted = Files.readAllBytes(Paths.get(metadata.getPath()));
            byte[] decrypted = decrypt(encrypted);
            return new FileDTO(metadata.getFilename(), decrypted);
        })
        .subscribeOn(Schedulers.boundedElastic());
    } // end of loadFile()

}