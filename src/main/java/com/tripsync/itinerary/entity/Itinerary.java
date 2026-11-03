package com.tripsync.itinerary.entity;

import com.tripsync.common.entity.BaseEntity;
import com.tripsync.trip.entity.Trip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "itineraries",uniqueConstraints = {
        @UniqueConstraint(name = "uk_itineraries_trip_date",columnNames = {"trip_id","itinerary_date"})
})
public class Itinerary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="trip_id",nullable = false)
    private Trip trip;

    @Column(name = "itinerary_date",nullable = false)
    private LocalDate itineraryDate;

    @Column(length = 1000)
    private String notes;
}
