package com.example.demo.modules.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
public class FileService{
    private final FileRepository fileRepository;
    private final Cryptor cryptor;
    private final String UPLOAD_DIR;

    

    public Mono<FileMetadata> saveFile(String filename, Mono<byte[]> fileBytesMono) {
        return fileBytesMono
        .map(cryptor::encrypt)
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
            byte[] decrypted = cryptor.decrypt(encrypted);
            return new FileDTO(metadata.getFilename(), decrypted);
        })
        .subscribeOn(Schedulers.boundedElastic());
    } // end of loadFile()

}