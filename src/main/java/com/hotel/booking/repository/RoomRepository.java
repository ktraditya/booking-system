package com.hotel.booking.repository;

import com.hotel.booking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(String roomNumber);
    Boolean existsByRoomNumber(String roomNumber);
    List<Room> findByIsAvailable(Boolean isAvailable);
    List<Room> findByType(Room.RoomType type);
    
    @Query("SELECT r FROM Room r WHERE r.isAvailable = true AND r.maintenanceStatus = 'AVAILABLE' " +
           "AND r.id NOT IN (SELECT b.room.id FROM Booking b WHERE " +
           "b.status NOT IN ('CANCELLED', 'COMPLETED') AND " +
           "((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)))")
    List<Room> findAvailableRoomsBetweenDates(
            @Param("checkIn") LocalDate checkIn, 
            @Param("checkOut") LocalDate checkOut);
    
    // Alias for the service layer
    default List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        return findAvailableRoomsBetweenDates(checkIn, checkOut);
    }
}
