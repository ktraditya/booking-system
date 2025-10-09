package com.hotel.booking.service;

import com.hotel.booking.dto.BookingRequest;
import com.hotel.booking.dto.BookingResponse;
import com.hotel.booking.dto.CancelBookingRequest;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Guest;
import com.hotel.booking.entity.Room;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.exception.BadRequestException;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.GuestRepository;
import com.hotel.booking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

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
        if (request.getCheckInDate().isAfter(request.getCheckOutDate()) ||
                request.getCheckInDate().isBefore(java.time.LocalDate.now())) {
            throw new BadRequestException("Invalid booking dates");
        }

        // Check room exists and is available
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));

        if (room.getMaintenanceStatus() != Room.MaintenanceStatus.AVAILABLE) {
            throw new BadRequestException("Room is not available for booking");
        }

        // Check room availability for the requested dates
        boolean isAvailable = bookingRepository.findConflictingBookings(
                request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate()).isEmpty();

        if (!isAvailable) {
            throw new BadRequestException("Room is not available for the selected dates");
        }

        // Check room capacity
        if (request.getNumberOfGuests() > room.getCapacity()) {
            throw new BadRequestException("Number of guests exceeds room capacity");
        }

        // Find or create guest
        Guest guest = null;
        if (request.getGuestId() != null) {
            guest = guestRepository.findById(request.getGuestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + request.getGuestId()));
        } else {
            // Check if guest exists by email
            Optional<Guest> existingGuest = guestRepository.findByEmail(request.getGuestEmail());
            if (existingGuest.isPresent()) {
                guest = existingGuest.get();
            } else {
                // Create new guest
                guest = createGuestFromBooking(request);
            }
        }

        // Create booking
        Booking booking = convertToEntity(request);
        booking.setRoom(room);
        booking.setGuest(guest);
        booking.setBookingNumber(generateBookingNumber());
        booking.setStatus(Booking.BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);
        return convertToResponse(savedBooking);
    }

    public BookingResponse updateBooking(Long id, BookingRequest request) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // Only allow updates if booking is pending
        if (existingBooking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BadRequestException("Can only update pending bookings");
        }

        // Validate new dates if room is changing
        if (!existingBooking.getRoom().getId().equals(request.getRoomId()) ||
                !existingBooking.getCheckInDate().equals(request.getCheckInDate()) ||
                !existingBooking.getCheckOutDate().equals(request.getCheckOutDate())) {
            
            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));

            boolean isAvailable = bookingRepository.findConflictingBookingsExcluding(
                    request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate(), id).isEmpty();

            if (!isAvailable) {
                throw new BadRequestException("Room is not available for the selected dates");
            }

            existingBooking.setRoom(room);
        }

        updateEntityFromRequest(existingBooking, request);
        Booking updatedBooking = bookingRepository.save(existingBooking);
        return convertToResponse(updatedBooking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long id, CancelBookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancellationReason(request.getReason());
        booking.setCancelledAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);
        return convertToResponse(savedBooking);
    }

    public BookingResponse confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BadRequestException("Can only confirm pending bookings");
        }

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);
        return convertToResponse(savedBooking);
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // Only allow deletion of cancelled or pending bookings
        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            throw new BadRequestException("Cannot delete confirmed bookings. Cancel first.");
        }

        bookingRepository.delete(booking);
    }

    private Guest createGuestFromBooking(BookingRequest request) {
        Guest guest = new Guest();
        // Parse first and last name from full name
        String[] nameParts = request.getGuestName().split(" ", 2);
        guest.setFirstName(nameParts[0]);
        guest.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        guest.setEmail(request.getGuestEmail());
        guest.setPhone(request.getGuestPhone());
        guest.setAddressStreet(request.getAddressStreet());
        guest.setAddressCity(request.getAddressCity());
        guest.setAddressState(request.getAddressState());
        guest.setAddressCountry(request.getAddressCountry());
        guest.setAddressPostalCode(request.getAddressPostalCode());
        return guestRepository.save(guest);
    }

    private String generateBookingNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "BK-" + timestamp;
    }

    private Booking convertToEntity(BookingRequest request) {
        Booking booking = new Booking();
        booking.setGuestName(request.getGuestName());
        booking.setGuestEmail(request.getGuestEmail());
        booking.setGuestPhone(request.getGuestPhone());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setSpecialRequests(request.getSpecialRequests());
        return booking;
    }

    private void updateEntityFromRequest(Booking booking, BookingRequest request) {
        booking.setGuestName(request.getGuestName());
        booking.setGuestEmail(request.getGuestEmail());
        booking.setGuestPhone(request.getGuestPhone());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setSpecialRequests(request.getSpecialRequests());
    }

    private BookingResponse convertToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingNumber(booking.getBookingNumber())
                .roomId(booking.getRoom().getId())
                .roomNumber(booking.getRoom().getRoomNumber())
                .guestId(booking.getGuest() != null ? booking.getGuest().getId() : null)
                .guestName(booking.getGuestName())
                .guestEmail(booking.getGuestEmail())
                .guestPhone(booking.getGuestPhone())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numberOfGuests(booking.getNumberOfGuests())
                .specialRequests(booking.getSpecialRequests())
                .status(booking.getStatus() != null ? booking.getStatus().name() : null)
                .totalPrice(booking.getTotalPrice())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}