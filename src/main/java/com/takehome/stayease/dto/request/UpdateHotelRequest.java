package com.takehome.stayease.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateHotelRequest {

  private String name;
  private String location;
  private String description;

  @Min(value = 1, message = "Total rooms must be at least 1")
  private Integer totalRooms;

  @Min(value = 0, message = "Available rooms cannot be negative")
  private Integer availableRooms;
}