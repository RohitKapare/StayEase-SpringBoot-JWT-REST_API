package com.takehome.stayease.dto.response;

import lombok.Data;

@Data
public class HotelResponse {

  private Long id;
  private String name;
  private String location;
  private String description;
  private Integer totalRooms;
  private Integer availableRooms;
}