package com.takehome.stayease.mapper;

import com.takehome.stayease.dto.request.CreateHotelRequest;
import com.takehome.stayease.dto.response.HotelResponse;
import com.takehome.stayease.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface HotelMapper {

  Hotel toEntity(CreateHotelRequest request);

  HotelResponse toResponse(Hotel hotel);

  void updateEntityFromRequest(
      com.takehome.stayease.dto.request.UpdateHotelRequest request,
      @MappingTarget Hotel hotel
  );
}