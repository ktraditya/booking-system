package com.hotel.booking.service;

import com.hotel.booking.dto.MessageRequest;
import com.hotel.booking.dto.MessageResponse;
import com.hotel.booking.dto.MessageResponseRequest;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Message;
import com.hotel.booking.entity.User;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.exception.BadRequestException;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.MessageRepository;
import com.hotel.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public MessageResponse sendMessage(MessageRequest request) {
        Message message = convertToEntity(request);
        message.setStatus(Message.MessageStatus.NEW);
        
        Message savedMessage = messageRepository.save(message);
        return convertToResponse(savedMessage);
    }

    public List<MessageResponse> getAllMessages() {
        return messageRepository.findByOrderByCreatedAtDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public MessageResponse getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        
        // Mark as read if it's new
        if (message.getStatus() == Message.MessageStatus.NEW) {
            message.setStatus(Message.MessageStatus.READ);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
        
        return convertToResponse(message);
    }

    public MessageResponse respondToMessage(Long id, MessageResponseRequest request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        if (message.getStatus() == Message.MessageStatus.RESPONDED) {
            throw new BadRequestException("Message has already been responded to");
        }

        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User respondingUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        message.setAdminResponse(request.getResponse());
        message.setRespondedAt(LocalDateTime.now());
        message.setAssignedTo(respondingUser);
        message.setStatus(Message.MessageStatus.RESPONDED);

        Message savedMessage = messageRepository.save(message);
        return convertToResponse(savedMessage);
    }

    public List<MessageResponse> getUnreadMessages() {
        return messageRepository.findByStatusOrderByCreatedAtDesc(Message.MessageStatus.NEW).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<MessageResponse> getMessagesByStatus(Message.MessageStatus status) {
        return messageRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        if (message.getStatus() == Message.MessageStatus.NEW) {
            message.setStatus(Message.MessageStatus.READ);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }

    public void markAsUnread(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        if (message.getStatus() != Message.MessageStatus.RESPONDED) {
            message.setStatus(Message.MessageStatus.NEW);
            message.setReadAt(null);
            messageRepository.save(message);
        }
    }

    private Message convertToEntity(MessageRequest request) {
        Message message = new Message();
        message.setSenderName(request.getSenderName());
        message.setSenderEmail(request.getSenderEmail());
        message.setSenderPhone(request.getSenderPhone());
        message.setSubject(request.getSubject());
        message.setContent(request.getContent());
        
        // Set type from string
        if (request.getType() != null) {
            try {
                message.setType(Message.MessageType.valueOf(request.getType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                message.setType(Message.MessageType.OTHER);
            }
        }
        
        // Set booking if provided
        if (request.getBookingId() != null) {
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));
            message.setBooking(booking);
        }
        
        return message;
    }

    private MessageResponse convertToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderName(message.getSenderName())
                .senderEmail(message.getSenderEmail())
                .senderPhone(message.getSenderPhone())
                .subject(message.getSubject())
                .content(message.getContent())
                .type(message.getType() != null ? message.getType().name() : null)
                .status(message.getStatus() != null ? message.getStatus().name() : null)
                .priority(message.getPriority() != null ? message.getPriority().name() : null)
                .bookingId(message.getBooking() != null ? message.getBooking().getId() : null)
                .bookingNumber(message.getBooking() != null ? message.getBooking().getBookingNumber() : null)
                .assignedToName(message.getAssignedTo() != null ? message.getAssignedTo().getName() : null)
                .adminResponse(message.getAdminResponse())
                .respondedAt(message.getRespondedAt())
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}