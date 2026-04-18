package com.takehome.stayease.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.takehome.stayease.service.impl.BookingServiceImpl;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Booking Service Tests")
class BookingServiceTest {

  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private HotelRepository hotelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private BookingMapper bookingMapper;

  @InjectMocks
  private BookingServiceImpl bookingService;

  private Hotel hotel;
  private User user;
  private Booking booking;

  @BeforeEach
  void setUp() {
    hotel = Hotel.builder()
        .id(1L)
        .name("Test Hotel")
        .availableRooms(5)
        .totalRooms(10)
        .build();

    user = User.builder()
        .id(1L)
        .email("user@example.com")
        .role(User.Role.USER)
        .build();

    booking = Booking.builder()
        .id(1L)
        .hotel(hotel)
        .user(user)
        .checkInDate(LocalDate.now().plusDays(1))
        .checkOutDate(LocalDate.now().plusDays(3))
        .status(Booking.Status.CONFIRMED)
        .build();
  }

  @Test
  @DisplayName("Create Booking - Success: available rooms decremented by 1")
  void createBooking_Success_DecreasesAvailableRooms() {
    CreateBookingRequest request = new CreateBookingRequest();
    request.setCheckInDate(LocalDate.now().plusDays(1));
    request.setCheckOutDate(LocalDate.now().plusDays(3));

    BookingResponse expectedResponse = new BookingResponse();
    expectedResponse.setBookingId(1L);
    expectedResponse.setHotelId(1L);

    when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
    when(bookingMapper.toResponse(any(Booking.class))).thenReturn(expectedResponse);

    BookingResponse result = bookingService.createBooking(1L, request, "user@example.com");

    assertNotNull(result);
    assertEquals(4, hotel.getAvailableRooms()); // decremented from 5
    verify(hotelRepository).save(hotel);
    verify(bookingRepository).save(any(Booking.class));
  }

  @Test
  @DisplayName("Create Booking - No rooms available: throws ResourceNotFoundException")
  void createBooking_NoRoomsAvailable_ThrowsException() {
    hotel.setAvailableRooms(0);

    CreateBookingRequest request = new CreateBookingRequest();
    request.setCheckInDate(LocalDate.now().plusDays(1));
    request.setCheckOutDate(LocalDate.now().plusDays(3));

    when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

    assertThrows(ResourceNotFoundException.class,
        () -> bookingService.createBooking(1L, request, "user@example.com"));
  }

  @Test
  @DisplayName("Create Booking - Check-out before check-in: throws BadRequestException")
  void createBooking_CheckoutBeforeCheckin_ThrowsBadRequest() {
    CreateBookingRequest request = new CreateBookingRequest();
    request.setCheckInDate(LocalDate.now().plusDays(3));
    request.setCheckOutDate(LocalDate.now().plusDays(1));

    when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

    assertThrows(BadRequestException.class,
        () -> bookingService.createBooking(1L, request, "user@example.com"));
  }

  @Test
  @DisplayName("Cancel Booking - Success: status set to CANCELLED and available rooms incremented by 1")
  void cancelBooking_Success_IncreasesAvailableRooms() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    bookingService.cancelBooking(1L);

    assertEquals(Booking.Status.CANCELLED, booking.getStatus());
    assertEquals(6, hotel.getAvailableRooms()); // incremented from 5
    verify(hotelRepository).save(hotel);
    verify(bookingRepository).save(booking);
  }

  @Test
  @DisplayName("Cancel Booking - Already cancelled: throws BadRequestException")
  void cancelBooking_AlreadyCancelled_ThrowsBadRequest() {
    booking.setStatus(Booking.Status.CANCELLED);
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    assertThrows(BadRequestException.class, () -> bookingService.cancelBooking(1L));
  }

  @Test
  @DisplayName("Get Booking - Not found: throws ResourceNotFoundException")
  void getBooking_NotFound_ThrowsException() {
    when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookingService.getBooking(99L));
  }
}