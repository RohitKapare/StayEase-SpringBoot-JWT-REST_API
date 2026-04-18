package com.takehome.stayease.service;

import com.takehome.stayease.dto.request.CreateHotelRequest;
import com.takehome.stayease.dto.request.UpdateHotelRequest;
import com.takehome.stayease.dto.response.HotelResponse;

import java.util.List;

public interface HotelService {

  HotelResponse createHotel(CreateHotelRequest request);

  List<HotelResponse> getAllHotels();

  HotelResponse updateHotel(Long hotelId, UpdateHotelRequest request);

  void deleteHotel(Long hotelId);
}