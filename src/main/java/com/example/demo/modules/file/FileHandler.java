package com.example.demo.modules.file;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class FileHandler {
    
    @Autowired
    private FileService fileService;
    @Autowired
    private BlobFileService blobFileService;
    
    public Mono<ServerResponse> download(ServerRequest request) {
        String id = request.pathVariable("id");
        return fileService.loadFile(id)
        .flatMap(fileDTO ->
        ServerResponse.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "attachment;filename=\""+fileDTO.getFilename()+"\"")
        .bodyValue(fileDTO.getData())
        .switchIfEmpty(ServerResponse.notFound().build())
        )
        ;
    } // end of download()
    
    public Mono<ServerResponse> upload(ServerRequest request) {
        return request.multipartData().
        flatMap(parts -> {
            FilePart filePart = (FilePart)parts.toSingleValueMap().get("file");
            if (filePart == null) {
                return ServerResponse.badRequest().bodyValue("Missing file part");
            }
            String filename = filePart.filename();
            Mono<byte[]> bytesMono = DataBufferUtils.join(filePart.content()).map(buffer -> {
                byte[] bytes= new byte[buffer.readableByteCount()];
                buffer.read(bytes);
                DataBufferUtils.release(buffer);
                return bytes;
            });
            return fileService
            .saveFile(filename, bytesMono)
            .flatMap(
            saved -> ServerResponse.ok().bodyValue(Map.of("id", saved.getFileId()))
            );
        });
    } // end of upload()
    
    
    public Mono<ServerResponse> download_blob (ServerRequest request) {
        String id = request.queryParam("id").orElseThrow(() -> new IllegalArgumentException("file id is required."));
        return blobFileService.loadFile(id)
        .flatMap(fileDTO ->
        ServerResponse.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "attachment;filename=\""+fileDTO.getFilename()+"\"")
        .bodyValue(fileDTO.getData())
        .switchIfEmpty(ServerResponse.notFound().build())
        )
        ;
    } // download_blob
    
    public Mono<ServerResponse> upload_blob(ServerRequest request) {
        return request.multipartData()
        .flatMap(parts -> {
            FilePart filePart =(FilePart)parts.toSingleValueMap().get("file");
            if (filePart == null) {
                return ServerResponse.badRequest().bodyValue("Missing filepart");
            }
            String filename = filePart.filename();
            Mono<byte[]> bytesMono = DataBufferUtils.join(filePart.content()).map(buffer -> {
                byte[] bytes = new byte[buffer.readableByteCount()];
                buffer.read(bytes);
                DataBufferUtils.release(buffer);
                return bytes;
            });
            return blobFileService
            .saveFile(filename, bytesMono)
            .flatMap(
            saved -> ServerResponse.ok().bodyValue(Map.of("id", saved.getFileId()))
            );
        });
    }// upload_blob()
    
    public Mono<ServerResponse> update_optional(ServerRequest request) {
        return request.multipartData()
        .flatMap(parts -> {
            Part filePart = parts.getFirst("file");
            if (filePart == null ) {
                return ServerResponse.badRequest().bodyValue("Missing filepart");
            }
            var file = (FilePart)filePart;
            var filename = file.filename();
            Mono<byte[]> bytesMono = DataBufferUtils.join(file.content()).map(buffer -> {
                byte[] bytes = new byte[buffer.readableByteCount()];
                buffer.read(bytes);
                DataBufferUtils.release(buffer);
                return bytes;
            });
            Part fileIdPart = parts.getFirst("file_id");
            if (fileIdPart != null) {
                var formField = (FormFieldPart) fileIdPart;
                return blobFileService.updateFile(formField.value(), filename, bytesMono)
                .flatMap(saved -> ServerResponse.ok().bodyValue(Map.of("id", saved.getFileId())));
            } else {
                return blobFileService
                .saveFile(filename, bytesMono)
                .flatMap(saved -> ServerResponse.ok().bodyValue(Map.of("id", saved.getFileId())));
            }
        });
        
    }// update_optional()
}