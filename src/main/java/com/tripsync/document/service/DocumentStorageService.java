package com.tripsync.document.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class DocumentStorageService {
    private final Path root;

    public DocumentStorageService(@Value("${app.document.storage-dir:uploads/documents}") String storageDirectory){
        this.root = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();
    }

    public String store(MultipartFile file){
        try{
            Files.createDirectories(root);
            String originalName = file.getOriginalFilename();
            String extension ="";
            if (originalName!=null){
                int dotIndex = originalName.lastIndexOf('.');
                if(dotIndex>=0){
                    extension = originalName.substring(dotIndex).replaceAll("[^A-Za-z0-9]","");

                }
            }
            String storageKey = UUID.randomUUID()+extension;
            Path target = root.resolve(storageKey).normalize();
            if (!target.startsWith(root)){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid file path");
            }
            Files.copy(file.getInputStream(),target);
            return storageKey;
        }catch (IOException exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unable to store the uploaded file");
        }
    }

    public Resource load(String storageKey){
        Path target = root.resolve(storageKey).normalize();
        if (!target.startsWith(root)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid file path");
        }
        Resource resource = new FileSystemResource(target);
        if (!resource.exists()||!resource.isReadable()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Store document file not found");
        }
        return resource;
    }

    public void delete(String storageKey){
        Path target = root.resolve(storageKey).normalize();
        if (!target.startsWith(root)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid file path");
        }
        try{
            Files.deleteIfExists(target);
        }catch (IOException exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unable to delete the stored file");
        }

    }
}
