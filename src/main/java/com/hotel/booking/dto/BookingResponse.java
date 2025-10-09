package com.hotel.booking.dto;

import com.hotel.booking.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingNumber;
    private Long roomId;
    private String roomNumber;
    private String roomType;
    private Long guestId;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private Integer numberOfNights;
    private Double totalPrice;
    private Double depositAmount;
    private Double remainingAmount;
    private String status;
    private String paymentStatus;
    private String specialRequests;
    private String cancellationPolicy;
    private String confirmationCode;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
