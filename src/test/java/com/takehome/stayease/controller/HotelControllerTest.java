package com.takehome.stayease.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takehome.stayease.config.JwtAuthFilter;
import com.takehome.stayease.dto.request.CreateHotelRequest;
import com.takehome.stayease.dto.response.HotelResponse;
import com.takehome.stayease.service.HotelService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HotelController.class)
@AutoConfigureMockMvc(addFilters = false)
class HotelControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  HotelService hotelService;
  @MockBean
  JwtAuthFilter jwtAuthFilter;
  @MockBean
  UserDetailsService userDetailsService;

  @Test
  void getAllHotels_public_returnsHotels() throws Exception {
    HotelResponse hotel = new HotelResponse();
    hotel.setId(1L);
    hotel.setName("Test Hotel");
    hotel.setLocation("Test City");
    hotel.setAvailableRooms(10);

    when(hotelService.getAllHotels()).thenReturn(List.of(hotel));

    mockMvc.perform(get("/api/hotels"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1));
  }

  @Test
  void createHotel_returnsCreatedHotel() throws Exception {
    CreateHotelRequest request = new CreateHotelRequest();
    request.setName("Grand Hotel");
    request.setLocation("NYC");
    request.setDescription("Luxury");
    request.setTotalRooms(10);
    request.setAvailableRooms(10);

    HotelResponse response = new HotelResponse();
    response.setId(1L);
    response.setName("Grand Hotel");
    response.setLocation("NYC");
    response.setTotalRooms(10);
    response.setAvailableRooms(10);

    when(hotelService.createHotel(any(CreateHotelRequest.class))).thenReturn(response);

    mockMvc.perform(post("/api/hotels")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }
}