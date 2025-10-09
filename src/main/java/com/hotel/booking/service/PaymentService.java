package com.hotel.booking.service;

import com.hotel.booking.dto.PaymentRequest;
import com.hotel.booking.dto.PaymentResponse;
import com.hotel.booking.dto.RefundRequest;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Payment;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.exception.BadRequestException;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        // Validate booking exists
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        // Check if booking is in a valid state for payment
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Cannot process payment for cancelled booking");
        }

        // Check if payment amount matches booking total
        if (booking.getTotalPrice() != null && 
                !booking.getTotalPrice().equals(request.getAmount())) {
            throw new BadRequestException("Payment amount does not match booking total");
        }

        // Create payment
        Payment payment = convertToEntity(request);
        payment.setBooking(booking);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());

        // Simulate payment processing
        processPayment(payment);

        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return convertToResponse(payment);
    }

    public List<PaymentResponse> getPaymentsByBookingId(Long bookingId) {
        // Verify booking exists
        if (!bookingRepository.existsById(bookingId)) {
            throw new ResourceNotFoundException("Booking not found with id: " + bookingId);
        }

        return paymentRepository.findByBookingId(bookingId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponse refundPayment(Long id, RefundRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        // Validate payment can be refunded
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new BadRequestException("Can only refund completed payments");
        }

        if (payment.getStatus() == Payment.PaymentStatus.REFUNDED) {
            throw new BadRequestException("Payment has already been refunded");
        }

        // Validate refund amount
        if (request.getAmount() != null && request.getAmount().compareTo(payment.getAmount()) > 0) {
            throw new BadRequestException("Refund amount cannot exceed original payment amount");
        }

        // Process refund
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setRefundAmount(request.getAmount() != null ? request.getAmount() : payment.getAmount());
        payment.setRefundReason(request.getReason());
        payment.setRefundedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private void processPayment(Payment payment) {
        // Simulate payment processing logic
        try {
            // In a real implementation, you would integrate with payment gateways
            // like Stripe, PayPal, etc.
            
            // For demo purposes, we'll just mark it as completed
            Thread.sleep(100); // Simulate processing time
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setProcessedAt(LocalDateTime.now());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.setStatus(Payment.PaymentStatus.FAILED);
            // Note: No setFailureReason method available in entity
        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            // Note: No setFailureReason method available in entity
        }
    }

    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    private Payment convertToEntity(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setCardLastFourDigits(request.getCardLastFourDigits());
        payment.setCardBrand(request.getCardBrand());
        payment.setBillingStreet(request.getBillingStreet());
        payment.setBillingCity(request.getBillingCity());
        payment.setBillingState(request.getBillingState());
        payment.setBillingPostalCode(request.getBillingPostalCode());
        payment.setBillingCountry(request.getBillingCountry());
        payment.setNotes(request.getNotes());
        return payment;
    }

    private PaymentResponse convertToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .bookingNumber(payment.getBooking().getBookingNumber())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null)
                .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                .transactionId(payment.getTransactionId())
                .processedAt(payment.getProcessedAt())
                .refundAmount(payment.getRefundAmount())
                .refundReason(payment.getRefundReason())
                .refundedAt(payment.getRefundedAt())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}