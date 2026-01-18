package com.ginamarieges.travelplans.repository;

import com.ginamarieges.travelplans.domain.City;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class InMemoryCityRepository implements CityRepository {

  private final Map<Integer, City> citiesById = new LinkedHashMap<>();
  private final Map<String, Integer> cityIdByNormalizedName = new LinkedHashMap<>();
  private int nextCityId = 1;

  @Override
  public synchronized Optional<City> findByName(String cityName) {
    if (cityName == null || cityName.trim().isEmpty()) {
      return Optional.empty();
    }
    String normalizedName = normalizeCityName(cityName);
    Integer cityId = cityIdByNormalizedName.get(normalizedName);
    if (cityId == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(citiesById.get(cityId));
  }


  @Override
  public synchronized City save(City city) {
    if (city == null) {
      throw new IllegalArgumentException("City must not be null");
    }
    if (city.getName() == null || city.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("City name must not be empty");
    }

    String normalizedName = normalizeCityName(city.getName());

    Integer existingCityIdByName = cityIdByNormalizedName.get(normalizedName);
    if (existingCityIdByName != null) {
      return citiesById.get(existingCityIdByName);
    }

    Integer cityId = city.getId();
    if (cityId != null) {
      City existingCityById = citiesById.get(cityId);
      if (existingCityById != null) {
        String existingNormalizedName = normalizeCityName(existingCityById.getName());
        if (!existingNormalizedName.equals(normalizedName)) {
          throw new IllegalArgumentException("City id already exists with a different name");
        }
        return existingCityById;
      }
    }

    if (cityId == null) {
      cityId = nextCityId++;
      city.setId(cityId);
    }

    citiesById.put(cityId, city);
    cityIdByNormalizedName.put(normalizedName, cityId);
    return city;
  }

  @Override
  public synchronized List<City> findAll() {
    return Collections.unmodifiableList(new ArrayList<>(citiesById.values()));
  }

  private static String normalizeCityName(String cityName) {
    if (cityName == null) {
      return "";
    }
    return cityName.trim().toUpperCase(Locale.ROOT);
  }
}
