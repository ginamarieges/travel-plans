package com.ginamarieges.travelplans.domain;

import java.time.LocalDateTime;

public class Plan {
  private Integer id;
  private String name;
  private PlanType type;
  private Integer totalAdults;
  private Integer totalKids;
  private City origin;
  private City destination;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Plan() {

  }

  public Plan(Plan other) {
      this.id = other.id;
      this.name = other.name;
      this.type = other.type;
      this.totalAdults = other.totalAdults;
      this.totalKids = other.totalKids;
      this.origin = other.origin;
      this.destination = other.destination;
      this.createdAt = other.createdAt;
      this.updatedAt = other.updatedAt;
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

  public PlanType getType() {
    return type;
  }
  public void setType(PlanType type) {
    this.type = type;
  }

  public Integer getTotalAdults() {
    return totalAdults;
  }
  public void setTotalAdults(Integer totalAdults) {
    this.totalAdults = totalAdults;
  }

  public Integer getTotalKids() {
    return totalKids;
  }
  public void setTotalKids(Integer totalKids) {
    this.totalKids = totalKids;
  }

  public City getOrigin() {
    return origin;
  }
  public void setOrigin(City origin) {
    this.origin = origin;
  }

  public City getDestination() {
    return destination;
  }
  public void setDestination(City destination) {
    this.destination = destination;
  }

  public LocalDateTime getCreatedAt() {
        return createdAt;
  }
  public void setCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
      return updatedAt;
  }
  public void setUpdatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
  }

   @Override
    public String toString() {
        return "Plan{id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", totalAdults=" + totalAdults +
                ", totalKids=" + totalKids +
                ", origin=" + origin +
                ", destination=" + destination +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
