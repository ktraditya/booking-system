package com.hotel.booking.dto;

import com.hotel.booking.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private String type;
    private Double pricePerNight;
    private Integer capacity;
    private Double size;
    private String bedType;
    private String description;
    private List<String> amenities;
    private List<String> images;
    private Integer floor;
    private String view;
    private Boolean smokingAllowed;
    private Boolean petFriendly;
    private Boolean isAvailable;
    private String maintenanceStatus;
    private Double rating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
