package com.takehome.stayease.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takehome.stayease.config.JwtAuthFilter;
import com.takehome.stayease.dto.response.BookingResponse;
import com.takehome.stayease.service.BookingService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  BookingService bookingService;
  @MockBean
  JwtAuthFilter jwtAuthFilter;
  @MockBean
  UserDetailsService userDetailsService;

  @Test
  @WithMockUser(username = "test@example.com", roles = "USER")
  void createBooking_ok() throws Exception {
    String json = """
        {
          "checkInDate": "2030-01-10",
          "checkOutDate": "2030-01-12"
        }
        """;

    BookingResponse response = new BookingResponse();
    response.setBookingId(1L);
    response.setHotelId(5L);
    response.setCheckInDate(LocalDate.parse("2030-01-10"));
    response.setCheckOutDate(LocalDate.parse("2030-01-12"));

    when(bookingService.createBooking(eq(5L), any(), anyString()))
        .thenReturn(response);

    mockMvc.perform(post("/api/bookings/5")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookingId").value(1))
        .andExpect(jsonPath("$.hotelId").value(5));
  }

  @Test
  void cancelBooking_noContent() throws Exception {
    mockMvc.perform(delete("/api/bookings/10"))
        .andExpect(status().isNoContent());
  }
}