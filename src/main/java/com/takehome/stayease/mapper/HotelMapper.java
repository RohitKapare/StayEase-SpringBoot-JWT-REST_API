package com.takehome.stayease.mapper;

import com.takehome.stayease.dto.request.CreateHotelRequest;
import com.takehome.stayease.dto.request.UpdateHotelRequest;
import com.takehome.stayease.dto.response.HotelResponse;
import com.takehome.stayease.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface HotelMapper {

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "createdAt", ignore = true),
      @Mapping(target = "bookings", ignore = true)
  })
  Hotel toEntity(CreateHotelRequest request);

  HotelResponse toResponse(Hotel hotel);

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "createdAt", ignore = true),
      @Mapping(target = "bookings", ignore = true)
  })
  void updateEntityFromRequest(UpdateHotelRequest request, @MappingTarget Hotel hotel);
}