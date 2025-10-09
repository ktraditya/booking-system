# Quick Implementation Guide - Copy & Paste Templates

## 🚀 Complete these files to finish the application

---

## SERVICE LAYER TEMPLATES

### 1. RoomService.java

```java
package com.hotel.booking.service;

import com.hotel.booking.dto.RoomRequest;
import com.hotel.booking.dto.RoomResponse;
import com.hotel.booking.entity.Room;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return convertToResponse(room);
    }

    public List<RoomResponse> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableRoomsBetweenDates(checkIn, checkOut).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setSize(request.getSize());
        room.setBedType(request.getBedType());
        room.setDescription(request.getDescription());
        room.setAmenities(request.getAmenities());
        room.setImages(request.getImages());
        room.setFloor(request.getFloor());
        room.setView(request.getView());
        room.setSmokingAllowed(request.getSmokingAllowed());
        room.setPetFriendly(request.getPetFriendly());
        room.setIsAvailable(true);
        
        Room savedRoom = roomRepository.save(room);
        return convertToResponse(savedRoom);
    }

    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        
        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setSize(request.getSize());
        room.setBedType(request.getBedType());
        room.setDescription(request.getDescription());
        room.setAmenities(request.getAmenities());
        room.setImages(request.getImages());
        room.setFloor(request.getFloor());
        room.setView(request.getView());
        room.setSmokingAllowed(request.getSmokingAllowed());
        room.setPetFriendly(request.getPetFriendly());
        
        Room updatedRoom = roomRepository.save(room);
        return convertToResponse(updatedRoom);
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
    }

    private RoomResponse convertToResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType().name())
                .pricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .size(room.getSize())
                .bedType(room.getBedType())
                .description(room.getDescription())
                .amenities(room.getAmenities())
                .images(room.getImages())
                .floor(room.getFloor())
                .view(room.getView())
                .smokingAllowed(room.getSmokingAllowed())
                .petFriendly(room.getPetFriendly())
                .isAvailable(room.getIsAvailable())
                .maintenanceStatus(room.getMaintenanceStatus().name())
                .rating(room.getRating())
                .reviewCount(room.getReviewCount())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
```

### 2. BookingService.java

```java
package com.hotel.booking.service;

import com.hotel.booking.dto.BookingRequest;
import com.hotel.booking.dto.BookingResponse;
import com.hotel.booking.dto.CancelBookingRequest;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Room;
import com.hotel.booking.exception.BadRequestException;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return convertToResponse(booking);
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // Validate dates
        if (request.getCheckInDate().isAfter(request.getCheckOutDate())) {
            throw new BadRequestException("Check-in date must be before check-out date");
        }

        // Get room
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));

        // Calculate nights and total price
        long numberOfNights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        double totalPrice = room.getPricePerNight() * numberOfNights;

        // Create booking
        Booking booking = new Booking();
        booking.setBookingNumber("BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setRoom(room);
        booking.setGuestName(request.getGuestName());
        booking.setGuestEmail(request.getGuestEmail());
        booking.setGuestPhone(request.getGuestPhone());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setNumberOfNights((int) numberOfNights);
        booking.setTotalPrice(totalPrice);
        booking.setSpecialRequests(request.getSpecialRequests());
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
        booking.setConfirmationCode(UUID.randomUUID().toString());
        
        // Set address if provided
        if (request.getAddressStreet() != null) {
            booking.setAddressStreet(request.getAddressStreet());
            booking.setAddressCity(request.getAddressCity());
            booking.setAddressState(request.getAddressState());
            booking.setAddressPostalCode(request.getAddressPostalCode());
            booking.setAddressCountry(request.getAddressCountry());
        }
        
        // Set emergency contact if provided
        if (request.getEmergencyContactName() != null) {
            booking.setEmergencyContactName(request.getEmergencyContactName());
            booking.setEmergencyContactPhone(request.getEmergencyContactPhone());
            booking.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
        }

        Booking savedBooking = bookingRepository.save(booking);
        return convertToResponse(savedBooking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long id, CancelBookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationReason(request.getReason());

        Booking updatedBooking = bookingRepository.save(booking);
        return convertToResponse(updatedBooking);
    }

    @Transactional
    public BookingResponse confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());

        Booking updatedBooking = bookingRepository.save(booking);
        return convertToResponse(updatedBooking);
    }

    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }

    private BookingResponse convertToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingNumber(booking.getBookingNumber())
                .roomId(booking.getRoom().getId())
                .roomNumber(booking.getRoom().getRoomNumber())
                .roomType(booking.getRoom().getType().name())
                .guestName(booking.getGuestName())
                .guestEmail(booking.getGuestEmail())
                .guestPhone(booking.getGuestPhone())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numberOfGuests(booking.getNumberOfGuests())
                .numberOfNights(booking.getNumberOfNights())
                .totalPrice(booking.getTotalPrice())
                .depositAmount(booking.getDepositAmount())
                .remainingAmount(booking.getRemainingAmount())
                .status(booking.getStatus().name())
                .paymentStatus(booking.getPaymentStatus().name())
                .specialRequests(booking.getSpecialRequests())
                .cancellationPolicy(booking.getCancellationPolicy())
                .confirmationCode(booking.getConfirmationCode())
                .confirmedAt(booking.getConfirmedAt())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
```

Continue in next message for MessageService and PaymentService...
