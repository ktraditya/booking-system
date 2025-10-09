package com.hotel.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private String senderName;
    private String senderEmail;
    private String senderPhone;
    private String subject;
    private String content;
    private String type;
    private String status;
    private String priority;
    private Long bookingId;
    private String bookingNumber;
    private String assignedToName;
    private String adminResponse;
    private LocalDateTime respondedAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
