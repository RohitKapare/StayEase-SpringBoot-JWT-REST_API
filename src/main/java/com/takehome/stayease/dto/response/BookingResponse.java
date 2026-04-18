package com.takehome.stayease.dto.response;

import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingResponse {

  private Long bookingId;
  private Long hotelId;
  private LocalDate checkInDate;
  private LocalDate checkOutDate;
}