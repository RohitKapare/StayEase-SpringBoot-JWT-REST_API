package com.takehome.stayease.mapper;

import com.takehome.stayease.dto.response.BookingResponse;
import com.takehome.stayease.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

  @Mapping(source = "id", target = "bookingId")
  @Mapping(source = "hotel.id", target = "hotelId")
  BookingResponse toResponse(Booking booking);
}