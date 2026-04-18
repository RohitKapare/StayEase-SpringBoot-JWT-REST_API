package com.takehome.stayease.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateHotelRequest {

  @NotBlank(message = "Hotel name is required")
  private String name;

  @NotBlank(message = "Location is required")
  private String location;

  private String description;

  @NotNull(message = "Total rooms is required")
  @Min(value = 1, message = "Total rooms must be at least 1")
  private Integer totalRooms;

  @NotNull(message = "Available rooms is required")
  @Min(value = 0, message = "Available rooms cannot be negative")
  private Integer availableRooms;
}