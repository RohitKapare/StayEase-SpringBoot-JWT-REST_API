package com.takehome.stayease.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateBookingRequest {

  @NotNull(message = "Check-in date is required")
  @Future(message = "Check-in date must be a future date")
  private LocalDate checkInDate;

  @NotNull(message = "Check-out date is required")
  private LocalDate checkOutDate;
}