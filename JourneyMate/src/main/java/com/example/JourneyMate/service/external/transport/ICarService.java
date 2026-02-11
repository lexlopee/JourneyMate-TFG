package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.cars.CarDTO;

import java.util.List;
import java.util.Map;

public interface ICarService {
    List<Map<String, Object>> searchCarLocation(String query);

    List<CarDTO> searchCars(Double pLat, Double pLon, Double dLat, Double dLon,
                            String pDate, String pTime, String dDate, String dTime,
                            Integer age, String currency);
}