package com.hotel.booking.repository;

import com.hotel.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingNumber(String bookingNumber);
    Optional<Booking> findByConfirmationCode(String confirmationCode);
    List<Booking> findByGuestEmail(String guestEmail);
    List<Booking> findByStatus(Booking.BookingStatus status);
    List<Booking> findByRoomId(Long roomId);
    Boolean existsByRoomIdAndStatus(Long roomId, Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.guest.id = :guestId ORDER BY b.createdAt DESC")
    List<Booking> findByGuestId(@Param("guestId") Long guestId);
    
    @Query("SELECT b FROM Booking b WHERE b.checkInDate >= :startDate AND b.checkOutDate <= :endDate")
    List<Booking> findBookingsBetweenDates(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND ((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
    List<Booking> findConflictingBookings(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);
    
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.id != :excludeBookingId " +
           "AND b.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND ((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
    List<Booking> findConflictingBookingsExcluding(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("excludeBookingId") Long excludeBookingId);
}
