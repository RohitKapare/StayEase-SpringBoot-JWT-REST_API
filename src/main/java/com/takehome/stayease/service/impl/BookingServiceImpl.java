package com.takehome.stayease.service.impl;

import com.takehome.stayease.dto.request.CreateBookingRequest;
import com.takehome.stayease.dto.response.BookingResponse;
import com.takehome.stayease.entity.Booking;
import com.takehome.stayease.entity.Hotel;
import com.takehome.stayease.entity.User;
import com.takehome.stayease.exception.BadRequestException;
import com.takehome.stayease.exception.ResourceNotFoundException;
import com.takehome.stayease.mapper.BookingMapper;
import com.takehome.stayease.repository.BookingRepository;
import com.takehome.stayease.repository.HotelRepository;
import com.takehome.stayease.repository.UserRepository;
import com.takehome.stayease.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

  private final BookingRepository bookingRepository;
  private final HotelRepository hotelRepository;
  private final UserRepository userRepository;
  private final BookingMapper bookingMapper;

  @Override
  @Transactional
  public BookingResponse createBooking(Long hotelId, CreateBookingRequest request, String userEmail) {
    log.info("Creating booking for hotel {} by user {}", hotelId, userEmail);

    if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
      throw new BadRequestException("Check-out date must be after check-in date");
    }

    Hotel hotel = hotelRepository.findById(hotelId)
        .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

    if (hotel.getAvailableRooms() <= 0) {
      throw new ResourceNotFoundException("No rooms available for hotel: " + hotelId);
    }

    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

    hotel.setAvailableRooms(hotel.getAvailableRooms() - 1);
    hotelRepository.save(hotel);

    Booking booking = Booking.builder()
        .hotel(hotel)
        .user(user)
        .checkInDate(request.getCheckInDate())
        .checkOutDate(request.getCheckOutDate())
        .status(Booking.Status.CONFIRMED)
        .build();

    Booking saved = bookingRepository.save(booking);
    log.info("Booking created with id: {}", saved.getId());
    return bookingMapper.toResponse(saved);
  }

  @Override
  public BookingResponse getBooking(Long bookingId) {
    log.debug("Fetching booking with id: {}", bookingId);
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    return bookingMapper.toResponse(booking);
  }

  @Override
  @Transactional
  public void cancelBooking(Long bookingId) {
    log.info("Cancelling booking with id: {}", bookingId);
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

    if (booking.getStatus() == Booking.Status.CANCELLED) {
      throw new BadRequestException("Booking is already cancelled");
    }

    Hotel hotel = booking.getHotel();
    hotel.setAvailableRooms(hotel.getAvailableRooms() + 1);
    hotelRepository.save(hotel);

    booking.setStatus(Booking.Status.CANCELLED);
    bookingRepository.save(booking);
    log.info("Booking cancelled: {}", bookingId);
  }
}