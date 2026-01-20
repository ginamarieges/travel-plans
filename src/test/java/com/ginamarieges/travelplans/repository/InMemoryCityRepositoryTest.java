package com.ginamarieges.travelplans.repository;

import com.ginamarieges.travelplans.domain.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryCityRepository")
class InMemoryCityRepositoryTest {

  private InMemoryCityRepository cityRepository;

  @BeforeEach
  void setUp() {
    cityRepository = new InMemoryCityRepository();
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should assign ID when missing")
    void save_assignsId_whenMissing() {
      City city = new City(null, "Madrid");
      
      City savedCity = cityRepository.save(city);

      assertNotNull(savedCity.getId());
      assertEquals("Madrid", savedCity.getName());
    }

    @Test
    @DisplayName("should not duplicate cities with same normalized name")
    void save_doesNotDuplicateCities_withSameNormalizedName() {
      City firstCity = cityRepository.save(new City(null, "Madrid"));
      City secondCity = cityRepository.save(new City(null, "  madrid "));

      assertEquals(firstCity.getId(), secondCity.getId());
      assertEquals(1, cityRepository.findAll().size());
    }

    @Test
    @DisplayName("should throw exception when city is null")
    void save_throwsException_whenCityIsNull() {
      assertThrows(IllegalArgumentException.class, () -> {
        cityRepository.save(null);
      });
    }

    @Test
    @DisplayName("should throw exception when city name is empty")
    void save_throwsException_whenNameIsEmpty() {
      City city = new City(null, "");

      assertThrows(IllegalArgumentException.class, () -> {
        cityRepository.save(city);
      });
    }

    @Test
    @DisplayName("should throw exception when city name is null")
    void save_throwsException_whenNameIsNull() {
      City city = new City(null, null);

      assertThrows(IllegalArgumentException.class, () -> {
        cityRepository.save(city);
      });
    }

    @Test
    @DisplayName("should return existing city when ID already exists with same name")
    void save_returnsExistingCity_whenIdExistsWithSameName() {
      City city = new City(null, "Barcelona");
      City savedCity = cityRepository.save(city);

      City cityWithSameId = new City(savedCity.getId(), "Barcelona");
      City result = cityRepository.save(cityWithSameId);

      assertEquals(savedCity.getId(), result.getId());
      assertEquals(savedCity.getName(), result.getName());
    }

    @Test
    @DisplayName("should throw exception when ID exists with different name")
    void save_throwsException_whenIdExistsWithDifferentName() {
      City city = new City(null, "Barcelona");
      City savedCity = cityRepository.save(city);

      City cityWithDifferentName = new City(savedCity.getId(), "Madrid");

      assertThrows(IllegalArgumentException.class, () -> {
        cityRepository.save(cityWithDifferentName);
      });
    }

    @Test
    @DisplayName("should assign sequential IDs")
    void save_assignsSequentialIds() {
      City city1 = cityRepository.save(new City(null, "Madrid"));
      City city2 = cityRepository.save(new City(null, "Barcelona"));
      City city3 = cityRepository.save(new City(null, "Valencia"));

      assertEquals(1, city1.getId());
      assertEquals(2, city2.getId());
      assertEquals(3, city3.getId());
    }
  }

  @Nested
  @DisplayName("findByName()")
  class FindByNameTests {

    @Test
    @DisplayName("should be case insensitive and trimmed")
    void findByName_isCaseInsensitiveAndTrimmed() {
      cityRepository.save(new City(null, "Madrid"));

      Optional<City> foundCity = cityRepository.findByName("  madrid  ");

      assertTrue(foundCity.isPresent());
      assertEquals("Madrid", foundCity.get().getName());
    }

    @Test
    @DisplayName("should return empty Optional when city not found")
    void findByName_returnsEmpty_whenCityNotFound() {
      Optional<City> foundCity = cityRepository.findByName("NonExistent");

      assertFalse(foundCity.isPresent());
    }

    @Test
    @DisplayName("should return empty Optional when name is null")
    void findByName_returnsEmpty_whenNameIsNull() {
      Optional<City> foundCity = cityRepository.findByName(null);

      assertFalse(foundCity.isPresent());
    }

    @Test
    @DisplayName("should return empty Optional when name is empty")
    void findByName_returnsEmpty_whenNameIsEmpty() {
      Optional<City> foundCity = cityRepository.findByName("");

      assertFalse(foundCity.isPresent());
    }

    @Test
    @DisplayName("should return empty Optional when name is blank")
    void findByName_returnsEmpty_whenNameIsBlank() {
      Optional<City> foundCity = cityRepository.findByName("   ");

      assertFalse(foundCity.isPresent());
    }


    @Test
    @DisplayName("should find city regardless of case")
    void findByName_findsCity_regardlessOfCase() {
      cityRepository.save(new City(null, "Barcelona"));

      assertTrue(cityRepository.findByName("BARCELONA").isPresent());
      assertTrue(cityRepository.findByName("barcelona").isPresent());
      assertTrue(cityRepository.findByName("BarCelOna").isPresent());
    }
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return empty list when no cities")
    void findAll_returnsEmptyList_whenNoCities() {
      List<City> cities = cityRepository.findAll();

      assertNotNull(cities);
      assertTrue(cities.isEmpty());
    }

    @Test
    @DisplayName("should return all saved cities")
    void findAll_returnsAllSavedCities() {
      cityRepository.save(new City(null, "Madrid"));
      cityRepository.save(new City(null, "Barcelona"));
      cityRepository.save(new City(null, "Valencia"));

      List<City> cities = cityRepository.findAll();

      assertEquals(3, cities.size());
    }

    @Test
    @DisplayName("should return unmodifiable list")
    void findAll_returnsUnmodifiableList() {
      cityRepository.save(new City(null, "Madrid"));
      List<City> cities = cityRepository.findAll();

      assertThrows(UnsupportedOperationException.class, () -> {
        cities.add(new City(null, "Barcelona"));
      });
    }

    @Test
    @DisplayName("should maintain insertion order")
    void findAll_maintainsInsertionOrder() {
      cityRepository.save(new City(null, "Madrid"));
      cityRepository.save(new City(null, "Barcelona"));
      cityRepository.save(new City(null, "Valencia"));

      List<City> cities = cityRepository.findAll();

      assertEquals("Madrid", cities.get(0).getName());
      assertEquals("Barcelona", cities.get(1).getName());
      assertEquals("Valencia", cities.get(2).getName());
    }
  }

  @Nested
  @DisplayName("Thread Safety")
  class ThreadSafetyTests {

    @Test
    @DisplayName("should handle concurrent saves without duplicates")
    void save_handlesConcurrentSaves_withoutDuplicates() throws InterruptedException {
      final int threadCount = 10;
      Thread[] threads = new Thread[threadCount];

      for (int i = 0; i < threadCount; i++) {
        threads[i] = new Thread(() -> {
          cityRepository.save(new City(null, "Madrid"));
        });
        threads[i].start();
      }

      for (Thread thread : threads) {
        thread.join();
      }

      // Should only have 1 Madrid (all concurrent saves should return the same city)
      List<City> cities = cityRepository.findAll();
      assertEquals(1, cities.size());
      assertEquals("Madrid", cities.get(0).getName());
    }
  }
}
