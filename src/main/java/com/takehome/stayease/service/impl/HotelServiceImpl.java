package com.takehome.stayease.service.impl;

import com.takehome.stayease.dto.request.CreateHotelRequest;
import com.takehome.stayease.dto.request.UpdateHotelRequest;
import com.takehome.stayease.dto.response.HotelResponse;
import com.takehome.stayease.entity.Hotel;
import com.takehome.stayease.exception.ResourceNotFoundException;
import com.takehome.stayease.mapper.HotelMapper;
import com.takehome.stayease.repository.HotelRepository;
import com.takehome.stayease.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;

  @Override
  @Transactional
  public HotelResponse createHotel(CreateHotelRequest request) {
    log.info("Creating hotel: {}", request.getName());
    Hotel hotel = hotelMapper.toEntity(request);
    Hotel saved = hotelRepository.save(hotel);
    log.info("Hotel created with id: {}", saved.getId());
    return hotelMapper.toResponse(saved);
  }

  @Override
  public List<HotelResponse> getAllHotels() {
    log.debug("Fetching all hotels");
    return hotelRepository.findAll()
        .stream()
        .map(hotelMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public HotelResponse updateHotel(Long hotelId, UpdateHotelRequest request) {
    log.info("Updating hotel with id: {}", hotelId);
    Hotel hotel = hotelRepository.findById(hotelId)
        .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
    hotelMapper.updateEntityFromRequest(request, hotel);
    Hotel updated = hotelRepository.save(hotel);
    log.info("Hotel updated successfully: {}", hotelId);
    return hotelMapper.toResponse(updated);
  }

  @Override
  @Transactional
  public void deleteHotel(Long hotelId) {
    log.info("Deleting hotel with id: {}", hotelId);
    if (!hotelRepository.existsById(hotelId)) {
      throw new ResourceNotFoundException("Hotel not found with id: " + hotelId);
    }
    hotelRepository.deleteById(hotelId);
    log.info("Hotel deleted: {}", hotelId);
  }
}