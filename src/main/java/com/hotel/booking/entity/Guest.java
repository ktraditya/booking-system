package com.hotel.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "guests")
@EntityListeners(AuditingEntityListener.class)
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    private LocalDate dateOfBirth;

    private String nationality;

    private String passportNumber;

    private String idNumber;

    // Address
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressPostalCode;
    private String addressCountry;

    // Preferences
    @Enumerated(EnumType.STRING)
    private RoomType preferredRoomType;

    private Boolean smokingPreference = false;

    private String floorPreference;

    @ElementCollection
    @CollectionTable(name = "guest_dietary_restrictions", joinColumns = @JoinColumn(name = "guest_id"))
    @Column(name = "restriction")
    private List<String> dietaryRestrictions = new ArrayList<>();

    @Column(length = 500)
    private String specialRequests;

    // Loyalty Program
    private Integer loyaltyPoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipTier membershipTier = MembershipTier.BRONZE;

    private Integer totalBookings = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum MembershipTier {
        BRONZE, SILVER, GOLD, PLATINUM
    }

    public enum RoomType {
        SINGLE, DOUBLE, SUITE, DELUXE
    }
}
