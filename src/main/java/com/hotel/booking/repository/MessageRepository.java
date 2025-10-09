package com.hotel.booking.repository;

import com.hotel.booking.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderEmail(String senderEmail);
    List<Message> findByStatus(Message.MessageStatus status);
    List<Message> findByType(Message.MessageType type);
    List<Message> findByBookingId(Long bookingId);
    List<Message> findByOrderByCreatedAtDesc();
    List<Message> findByStatusOrderByCreatedAtDesc(Message.MessageStatus status);
}
