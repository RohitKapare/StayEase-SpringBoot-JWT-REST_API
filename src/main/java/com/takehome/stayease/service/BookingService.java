package com.takehome.stayease.service;

import com.takehome.stayease.dto.request.CreateBookingRequest;
import com.takehome.stayease.dto.response.BookingResponse;

public interface BookingService {

  BookingResponse createBooking(Long hotelId, CreateBookingRequest request, String userEmail);

  BookingResponse getBooking(Long bookingId);

  void cancelBooking(Long bookingId);
}