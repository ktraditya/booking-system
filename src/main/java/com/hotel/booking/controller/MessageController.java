package com.hotel.booking.controller;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.dto.MessageRequest;
import com.hotel.booking.dto.MessageResponse;
import com.hotel.booking.dto.MessageResponseRequest;
import com.hotel.booking.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management", description = "APIs for managing guest messages and communication")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Send message", description = "Send a message to hotel staff")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(@Valid @RequestBody MessageRequest request) {
        MessageResponse message = messageService.sendMessage(request);
        return ResponseEntity.ok(ApiResponse.success("Message sent successfully", message));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all messages", description = "Retrieve all messages (Admin only)")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getAllMessages() {
        List<MessageResponse> messages = messageService.getAllMessages();
        return ResponseEntity.ok(ApiResponse.success("Messages retrieved successfully", messages));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get message by ID", description = "Retrieve a specific message by its ID (Admin only)")
    public ResponseEntity<ApiResponse<MessageResponse>> getMessageById(@PathVariable Long id) {
        MessageResponse message = messageService.getMessageById(id);
        return ResponseEntity.ok(ApiResponse.success("Message retrieved successfully", message));
    }

    @PutMapping("/{id}/respond")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Respond to message", description = "Respond to a guest message (Admin only)")
    public ResponseEntity<ApiResponse<MessageResponse>> respondToMessage(
            @PathVariable Long id,
            @Valid @RequestBody MessageResponseRequest request) {
        MessageResponse message = messageService.respondToMessage(id, request);
        return ResponseEntity.ok(ApiResponse.success("Response sent successfully", message));
    }

    @GetMapping("/unread")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get unread messages", description = "Retrieve all unread messages (Admin only)")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getUnreadMessages() {
        List<MessageResponse> messages = messageService.getUnreadMessages();
        return ResponseEntity.ok(ApiResponse.success("Unread messages retrieved successfully", messages));
    }

    @PutMapping("/{id}/mark-read")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark message as read", description = "Mark a message as read (Admin only)")
    public ResponseEntity<ApiResponse<Object>> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Message marked as read", null));
    }

    @PutMapping("/{id}/mark-unread")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark message as unread", description = "Mark a message as unread (Admin only)")
    public ResponseEntity<ApiResponse<Object>> markAsUnread(@PathVariable Long id) {
        messageService.markAsUnread(id);
        return ResponseEntity.ok(ApiResponse.success("Message marked as unread", null));
    }
}