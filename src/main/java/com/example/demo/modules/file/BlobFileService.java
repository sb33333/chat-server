package com.example.demo.modules.file;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
public class BlobFileService{
    
    private final BlobFileRepository repository;
    private final Cryptor cryptor;
    
    public Mono<BlobFile> saveFile (String filename, Mono<byte[]> fileBytesMono) {
        return fileBytesMono
        .map(cryptor::encrypt)
        .flatMap(encrypted ->
        Mono.fromCallable(() -> {
            try {
                var blobFile = new BlobFile();
                blobFile.setFileId(UUID.randomUUID().toString());
                blobFile.setFilename(filename);
                blobFile.setData(encrypted);
                return repository.save(blobFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        })
        )
        .subscribeOn(Schedulers.boundedElastic());
    }// saveFile()
    
    public Mono<FileDTO> loadFile(String id) {
        return Mono.fromCallable(() ->{
            BlobFile file = repository.findById(id).orElseThrow(()-> new IllegalAccessError("File not found."));
            byte[] decrypted = cryptor.decrypt(file.getData());
            return new FileDTO(file.getFileIdWithExtension(), decrypted);
        })
        .subscribeOn(Schedulers.boundedElastic())
        ;
    } // loadFile()
    
    public Mono<Void> deleteFile(String id) {
        return Mono.fromRunnable(() -> {
            repository.deleteById(id);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }//deleteFile()
    
    public Mono<BlobFile> updateFile(String id, String filename, Mono<byte[]> fileBytesMono) {
        return fileBytesMono
        .map(cryptor::encrypt)
        .flatMap(encrypted ->
        Mono.fromCallable(() -> {
            try {
                var blobFile =repository.findById(id).orElseThrow(() -> new IllegalAccessError("File not found."));
                blobFile.setFilename(filename);
                blobFile.setData(encrypted);
                return repository.save(blobFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        })
        )
        .subscribeOn(Schedulers.boundedElastic());
    }//updateFile()
}