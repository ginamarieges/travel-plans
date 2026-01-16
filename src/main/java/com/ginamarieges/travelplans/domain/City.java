package com.ginamarieges.travelplans.domain;

import java.util.Objects;

public class City {
  private Integer id;
  private String name;

  public City(){

  }

  public City(Integer id, String name) {
    this.id = id;
    this.name = name;
  }

  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * We override equals to decide when two City objects represent
   * the same logical city.
   *
   * - If both cities already have an ID, we compare by ID.
   *
   * - If there is no ID yet,
   *   we compare by name to avoid duplicated cities.
   */
  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
        return true;
    }
    if (!(otherObject instanceof City)) {
        return false;
    }
    City otherCity = (City) otherObject;

    if (this.id != null && otherCity.id != null) {
        return Objects.equals(this.id, otherCity.id);
    }

    return Objects.equals(this.name, otherCity.name);
  }

  @Override
  public int hashCode() {
    if (id != null) {
        return Objects.hash(id);
    }
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "City{id=" + id + ", name='" + name + "'}";
  }
}
