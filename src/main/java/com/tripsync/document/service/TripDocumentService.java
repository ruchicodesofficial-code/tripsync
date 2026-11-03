package com.tripsync.document.service;

import com.tripsync.document.dto.DocumentDownload;
import com.tripsync.document.dto.DocumentResponse;
import com.tripsync.document.entity.TripDocument;
import com.tripsync.document.repository.TripDocumentRepository;
import com.tripsync.trip.entity.Trip;
import com.tripsync.trip.repository.TripMemberRepository;
import com.tripsync.trip.repository.TripRepository;
import com.tripsync.user.entity.User;
import com.tripsync.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TripDocumentService {
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/webp",
            "text/plain",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    private final TripDocumentRepository documentRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final UserRepository userRepository;
    private final DocumentStorageService storageService;
    @Value("${app.document.max-size-bytes:10485760}")
    private long maxFileSize;

    @Transactional
    public DocumentResponse upload(Long tripId, MultipartFile file,String email){
        User user = getUser(email);
        Trip trip = getTrip(tripId);
        ensureTripAccess(trip,user);
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A non-empty file is required");
        }
        if (file.getSize()>maxFileSize){
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,"File exceeds the allowed size");
        }
        String contentType = file.getContentType();
        if (contentType==null||!ALLOWED_TYPES.contains(contentType)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unsupported file type");
        }
        String originalName = file.getOriginalFilename();
        if (originalName==null||originalName.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Original filename is required");
        }
        String storageKey = storageService.store(file);
        try{
            TripDocument document = new TripDocument();
            document.setTrip(trip);
            document.setUploadedBy(user);
            document.setOriginalFilename(originalName.substring(Math.max(0,originalName.lastIndexOf('/')+1)));
            document.setStorageKey(storageKey);
            document.setContentType(contentType);
            document.setFileSize(file.getSize());
            return toResponse(documentRepository.save(document));

        }catch (RuntimeException exception){
            storageService.delete(storageKey);
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> list(Long tripId,String email){
        User user = getUser(email);
        Trip trip = getTrip(tripId);
        ensureTripAccess(trip,user);
        return documentRepository.findAllByTrip_IdOrderByCreatedAtDesc(tripId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DocumentDownload documentDownload(Long tripId, Long documentId,String email){
        User user = getUser(email);
        Trip trip = getTrip(tripId);
        ensureTripAccess(trip,user);
        TripDocument document = documentRepository.findByIdAndTrip_Id(documentId,tripId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Document not found"));
        return new DocumentDownload(
                storageService.load(document.getStorageKey()),
                document.getOriginalFilename(),
                document.getContentType()
        );
    }

    @Transactional
    public void delete(Long tripId,Long documentId,String email){
        User user = getUser(email);
        Trip trip = getTrip(tripId);
        ensureTripAccess(trip,user);
        TripDocument document = documentRepository.findByIdAndTrip_Id(documentId,tripId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Document not found"));
     boolean isOwner = trip.getOwner().getId().equals(user.getId());
     boolean uploadByCurrentUser = document.getUploadedBy().getId().equals(user.getId());
     if (!isOwner&&!uploadByCurrentUser){
         throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the trip owner or uploader can delete this document");
     }
     storageService.delete(document.getStorageKey());
     documentRepository.delete(document);
    }

    private User getUser(String  email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
    }

    private  Trip getTrip(Long tripId){
        return tripRepository.findById(tripId)
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,"Trip not found"));
    }
    private void ensureTripAccess(Trip trip,User user){
        boolean isOwner = trip.getOwner().getId().equals(user.getId());
        boolean isMember = tripMemberRepository.existsByTrip_IdAndUser_Id(trip.getId(),user.getId());
        if (!isOwner&&!isMember){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You do not have access to this trip");
        }
    }

    private DocumentResponse toResponse(TripDocument document){
        return new DocumentResponse(
                document.getId(),
                document.getTrip().getId(),
                document.getUploadedBy().getId(),
                document.getOriginalFilename(),
                document.getContentType(),
                document.getFileSize(),
                document.getCreatedAt()
        );
    }
}
