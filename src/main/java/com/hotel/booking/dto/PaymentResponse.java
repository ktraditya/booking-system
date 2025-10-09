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
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private String bookingNumber;
    private Double amount;
    private String currency;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private String cardLastFourDigits;
    private String cardBrand;
    private Double refundAmount;
    private LocalDateTime refundedAt;
    private String refundReason;
    private String notes;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
