package com.tripsync.document.controller;

import com.tripsync.document.dto.DocumentDownload;
import com.tripsync.document.dto.DocumentResponse;
import com.tripsync.document.service.TripDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips/{tripId}/documents")
public class TripDocumentController {
    private final TripDocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> upload(@PathVariable Long tripId,
                                                   @RequestParam("file")MultipartFile file,
                                                   Authentication authentication){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                documentService.upload(tripId,file,getEmail(authentication))
        );
    }

    @GetMapping
    public List<DocumentResponse> list(@PathVariable Long tripId,Authentication authentication){
        return documentService.list(tripId,getEmail(authentication));
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<org.springframework.core.io.Resource> download(@PathVariable Long tripId,
                                                                         @PathVariable Long documentId,
                                                                         Authentication authentication){
        DocumentDownload download = documentService.documentDownload(tripId,documentId,getEmail(authentication));
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(download.originalFilename(),
                        StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,disposition.toString()
                ).body(download.resource());

    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> delete(@PathVariable Long tripId,
                                       @PathVariable Long documentId,
                                       Authentication authentication){
        documentService.delete(tripId,documentId,getEmail(authentication));
        return ResponseEntity.noContent().build();
    }

    private String getEmail(Authentication authentication){
        if (authentication==null||!authentication.isAuthenticated()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication is required");
        }
        return authentication.getName();
    }
}
