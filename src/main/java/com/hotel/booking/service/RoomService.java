package com.hotel.booking.service;

import com.hotel.booking.dto.RoomRequest;
import com.hotel.booking.dto.RoomResponse;
import com.hotel.booking.entity.Room;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.exception.BadRequestException;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

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
        if (checkIn == null || checkOut == null) {
            throw new BadRequestException("Check-in and check-out dates are required");
        }
        if (checkIn.isAfter(checkOut) || checkIn.isBefore(LocalDate.now())) {
            throw new BadRequestException("Invalid date range");
        }

        List<Room> availableRooms = roomRepository.findAvailableRooms(checkIn, checkOut);
        return availableRooms.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public RoomResponse createRoom(RoomRequest request) {
        // Check if room number already exists
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new BadRequestException("Room number already exists: " + request.getRoomNumber());
        }

        Room room = convertToEntity(request);
        room.setMaintenanceStatus(Room.MaintenanceStatus.AVAILABLE);
        Room savedRoom = roomRepository.save(room);
        return convertToResponse(savedRoom);
    }

    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        // Check if room number is being changed and if it already exists
        if (!existingRoom.getRoomNumber().equals(request.getRoomNumber()) &&
                roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new BadRequestException("Room number already exists: " + request.getRoomNumber());
        }

        updateEntityFromRequest(existingRoom, request);
        Room updatedRoom = roomRepository.save(existingRoom);
        return convertToResponse(updatedRoom);
    }

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        // Check if room has active bookings
        boolean hasActiveBookings = bookingRepository.existsByRoomIdAndStatus(id, 
                com.hotel.booking.entity.Booking.BookingStatus.CONFIRMED);
        
        if (hasActiveBookings) {
            throw new BadRequestException("Cannot delete room with active bookings");
        }

        roomRepository.delete(room);
    }

    private Room convertToEntity(RoomRequest request) {
        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setSize(request.getSize());
        room.setBedType(request.getBedType());
        room.setDescription(request.getDescription());
        room.setAmenities(request.getAmenities() != null ? request.getAmenities() : new ArrayList<>());
        room.setImages(request.getImages() != null ? request.getImages() : new ArrayList<>());
        room.setFloor(request.getFloor());
        room.setView(request.getView());
        room.setSmokingAllowed(request.getSmokingAllowed() != null ? request.getSmokingAllowed() : false);
        room.setPetFriendly(request.getPetFriendly() != null ? request.getPetFriendly() : false);
        room.setIsAvailable(true);
        room.setMaintenanceStatus(Room.MaintenanceStatus.AVAILABLE);
        return room;
    }

    private void updateEntityFromRequest(Room room, RoomRequest request) {
        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setSize(request.getSize());
        room.setBedType(request.getBedType());
        room.setDescription(request.getDescription());
        room.setAmenities(request.getAmenities() != null ? request.getAmenities() : new ArrayList<>());
        room.setImages(request.getImages() != null ? request.getImages() : new ArrayList<>());
        room.setFloor(request.getFloor());
        room.setView(request.getView());
        room.setSmokingAllowed(request.getSmokingAllowed() != null ? request.getSmokingAllowed() : false);
        room.setPetFriendly(request.getPetFriendly() != null ? request.getPetFriendly() : false);
    }

    private RoomResponse convertToResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType() != null ? room.getType().name() : null)
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
                .maintenanceStatus(room.getMaintenanceStatus() != null ? room.getMaintenanceStatus().name() : null)
                .rating(room.getRating())
                .reviewCount(room.getReviewCount())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}