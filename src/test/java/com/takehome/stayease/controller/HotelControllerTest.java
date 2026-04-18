package com.takehome.stayease.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takehome.stayease.dto.request.CreateHotelRequest;
import com.takehome.stayease.dto.response.HotelResponse;
import com.takehome.stayease.service.HotelService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HotelController.class)
@DisplayName("Hotel Controller Tests")
class HotelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private HotelService hotelService;

  @Test
  @DisplayName("GET /api/hotels - Public endpoint returns list of hotels")
  void getAllHotels_PublicEndpoint_ReturnsHotels() throws Exception {
    HotelResponse hotel = new HotelResponse();
    hotel.setId(1L);
    hotel.setName("Test Hotel");
    hotel.setLocation("Test City");
    hotel.setAvailableRooms(10);

    when(hotelService.getAllHotels()).thenReturn(List.of(hotel));

    mockMvc.perform(get("/api/hotels"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Test Hotel"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("POST /api/hotels - Admin creates a hotel and gets 200 with hotel details")
  void createHotel_AsAdmin_ReturnsCreatedHotel() throws Exception {
    CreateHotelRequest request = new CreateHotelRequest();
    request.setName("Grand Hotel");
    request.setLocation("New York");
    request.setDescription("Luxury hotel");
    request.setTotalRooms(50);
    request.setAvailableRooms(50);

    HotelResponse response = new HotelResponse();
    response.setId(1L);
    response.setName("Grand Hotel");
    response.setLocation("New York");
    response.setTotalRooms(50);
    response.setAvailableRooms(50);

    when(hotelService.createHotel(any(CreateHotelRequest.class))).thenReturn(response);

    mockMvc.perform(post("/api/hotels")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Grand Hotel"));
  }

  @Test
  @WithMockUser(roles = "USER")
  @DisplayName("POST /api/hotels - Regular user is forbidden from creating a hotel")
  void createHotel_AsUser_ReturnsForbidden() throws Exception {
    CreateHotelRequest request = new CreateHotelRequest();
    request.setName("Grand Hotel");
    request.setLocation("New York");
    request.setTotalRooms(50);
    request.setAvailableRooms(50);

    mockMvc.perform(post("/api/hotels")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }
}