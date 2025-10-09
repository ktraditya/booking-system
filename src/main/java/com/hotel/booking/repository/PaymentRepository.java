package com.hotel.booking.repository;

import com.hotel.booking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
}
