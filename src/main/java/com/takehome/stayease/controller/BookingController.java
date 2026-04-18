package com.takehome.stayease.controller;

import com.takehome.stayease.dto.request.CreateBookingRequest;
import com.takehome.stayease.dto.response.BookingResponse;
import com.takehome.stayease.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

  private final BookingService bookingService;

  @PostMapping("/{hotelId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<BookingResponse> createBooking(
      @PathVariable Long hotelId,
      @Valid @RequestBody CreateBookingRequest request,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    log.info("POST /api/bookings/{} by user {}", hotelId, userDetails.getUsername());
    return ResponseEntity.ok(bookingService.createBooking(hotelId, request, userDetails.getUsername()));
  }

  @GetMapping("/{bookingId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<BookingResponse> getBooking(@PathVariable Long bookingId) {
    log.info("GET /api/bookings/{}", bookingId);
    return ResponseEntity.ok(bookingService.getBooking(bookingId));
  }

  @DeleteMapping("/{bookingId}")
  @PreAuthorize("hasRole('HOTEL_MANAGER')")
  public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
    log.info("DELETE /api/bookings/{}", bookingId);
    bookingService.cancelBooking(bookingId);
    return ResponseEntity.noContent().build();
  }
}