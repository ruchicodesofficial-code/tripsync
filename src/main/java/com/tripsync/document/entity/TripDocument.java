package com.tripsync.document.entity;

import com.tripsync.common.entity.BaseEntity;
import com.tripsync.trip.entity.Trip;
import com.tripsync.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="documents")
public class TripDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "trip_id",nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id",nullable = false)
    private User uploadedBy;

    @Column(name = "original_filename",nullable = false,length = 255)
    private String originalFilename;

    @Column(name = "storage_key",nullable = false,unique = true,length = 100)
    private String storageKey;

    @Column(name = "content_type",nullable = false,length = 150)
    private String contentType;

    @NotNull
    @Positive
    @Column(name="file_size",nullable = false)
    private Long fileSize;
}
