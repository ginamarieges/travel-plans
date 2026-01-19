package com.ginamarieges.travelplans.repository;

import com.ginamarieges.travelplans.domain.City;

import java.util.List;
import java.util.Optional;

public interface CityRepository {

  Optional<City> findByName(String cityName);

  City save(City city);

  List<City> findAll();
}
