package com.takehome.stayease.controller;

import com.takehome.stayease.dto.request.CreateHotelRequest;
import com.takehome.stayease.dto.request.UpdateHotelRequest;
import com.takehome.stayease.dto.response.HotelResponse;
import com.takehome.stayease.service.HotelService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

  private final HotelService hotelService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<HotelResponse> createHotel(@Valid @RequestBody CreateHotelRequest request) {
    log.info("POST /api/hotels");
    return ResponseEntity.ok(hotelService.createHotel(request));
  }

  @GetMapping
  public ResponseEntity<List<HotelResponse>> getAllHotels() {
    log.info("GET /api/hotels");
    return ResponseEntity.ok(hotelService.getAllHotels());
  }

  @PutMapping("/{hotelId}")
  @PreAuthorize("hasRole('HOTEL_MANAGER')")
  public ResponseEntity<HotelResponse> updateHotel(
      @PathVariable Long hotelId,
      @Valid @RequestBody UpdateHotelRequest request
  ) {
    log.info("PUT /api/hotels/{}", hotelId);
    return ResponseEntity.ok(hotelService.updateHotel(hotelId, request));
  }

  @DeleteMapping("/{hotelId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId) {
    log.info("DELETE /api/hotels/{}", hotelId);
    hotelService.deleteHotel(hotelId);
    return ResponseEntity.noContent().build();
  }
}