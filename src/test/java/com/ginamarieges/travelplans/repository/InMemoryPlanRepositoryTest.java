package com.ginamarieges.travelplans.repository;

import com.ginamarieges.travelplans.domain.City;
import com.ginamarieges.travelplans.domain.Plan;
import com.ginamarieges.travelplans.domain.PlanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryPlanRepository")
class InMemoryPlanRepositoryTest {

  private InMemoryPlanRepository planRepository;
  private City madrid;
  private City barcelona;

  @BeforeEach
  void setUp() {
    planRepository = new InMemoryPlanRepository();
    madrid = new City(1, "Madrid");
    barcelona = new City(2, "Barcelona");
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should assign ID when creating new plan")
    void save_assignsId_whenCreatingNewPlan() {
      Plan plan = createPlan(null, "Vacation Plan");

      Plan savedPlan = planRepository.save(plan);

      assertNotNull(savedPlan.getId());
      assertEquals("Vacation Plan", savedPlan.getName());
    }

    @Test
    @DisplayName("should assign sequential IDs")
    void save_assignsSequentialIds() {
      Plan plan1 = planRepository.save(createPlan(null, "Plan 1"));
      Plan plan2 = planRepository.save(createPlan(null, "Plan 2"));
      Plan plan3 = planRepository.save(createPlan(null, "Plan 3"));

      assertEquals(1, plan1.getId());
      assertEquals(2, plan2.getId());
      assertEquals(3, plan3.getId());
    }

    @Test
    @DisplayName("should set createdAt when creating new plan")
    void save_setsCreatedAt_whenCreatingNewPlan() {
      Plan plan = createPlan(null, "Vacation Plan");
      LocalDateTime beforeSave = LocalDateTime.now().minusSeconds(1);

      Plan savedPlan = planRepository.save(plan);

      assertNotNull(savedPlan.getCreatedAt());
      assertTrue(savedPlan.getCreatedAt().isAfter(beforeSave));
    }

    @Test
    @DisplayName("should set updatedAt when creating new plan")
    void save_setsUpdatedAt_whenCreatingNewPlan() {
      Plan plan = createPlan(null, "Vacation Plan");
      LocalDateTime beforeSave = LocalDateTime.now().minusSeconds(1);

      Plan savedPlan = planRepository.save(plan);

      assertNotNull(savedPlan.getUpdatedAt());
      assertTrue(savedPlan.getUpdatedAt().isAfter(beforeSave));
    }

    @Test
    @DisplayName("should update existing plan when ID is present")
    void save_updatesExistingPlan_whenIdIsPresent() {
      Plan plan = createPlan(null, "Original Name");
      Plan savedPlan = planRepository.save(plan);

      savedPlan.setName("Updated Name");
      Plan updatedPlan = planRepository.save(savedPlan);

      assertEquals(savedPlan.getId(), updatedPlan.getId());
      assertEquals("Updated Name", updatedPlan.getName());
    }

    @Test
    @DisplayName("should preserve createdAt when updating")
    void save_preservesCreatedAt_whenUpdating() throws InterruptedException {
      Plan plan = createPlan(null, "Original Plan");
      Plan savedPlan = planRepository.save(plan);
      LocalDateTime originalCreatedAt = savedPlan.getCreatedAt();

      Thread.sleep(10); // Ensure time difference

      savedPlan.setName("Updated Plan");
      Plan updatedPlan = planRepository.save(savedPlan);

      assertEquals(originalCreatedAt, updatedPlan.getCreatedAt());
    }

    @Test
    @DisplayName("should update updatedAt when updating")
    void save_updatesUpdatedAt_whenUpdating() throws InterruptedException {
      Plan plan = createPlan(null, "Original Plan");
      Plan savedPlan = planRepository.save(plan);
      LocalDateTime originalUpdatedAt = savedPlan.getUpdatedAt();

      Thread.sleep(10); // Ensure time difference

      savedPlan.setName("Updated Plan");
      Plan updatedPlan = planRepository.save(savedPlan);

      assertNotNull(updatedPlan.getUpdatedAt());
      assertTrue(updatedPlan.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("should throw exception when plan is null")
    void save_throwsException_whenPlanIsNull() {
      assertThrows(IllegalArgumentException.class, () -> {
        planRepository.save(null);
      });
    }

    @Test
    @DisplayName("should set createdAt if missing on update")
    void save_setsCreatedAt_ifMissingOnUpdate() {
      Plan plan = createPlan(1, "Plan with ID");
      plan.setCreatedAt(null);

      Plan savedPlan = planRepository.save(plan);

      assertNotNull(savedPlan.getCreatedAt());
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return plan when it exists")
    void findById_returnsPlan_whenExists() {
      Plan plan = createPlan(null, "Vacation Plan");
      Plan savedPlan = planRepository.save(plan);

      Optional<Plan> foundPlan = planRepository.findById(savedPlan.getId());

      assertTrue(foundPlan.isPresent());
      assertEquals(savedPlan.getId(), foundPlan.get().getId());
      assertEquals("Vacation Plan", foundPlan.get().getName());
    }

    @Test
    @DisplayName("should return empty when plan does not exist")
    void findById_returnsEmpty_whenPlanDoesNotExist() {
      Optional<Plan> foundPlan = planRepository.findById(999);

      assertFalse(foundPlan.isPresent());
    }

    @Test
    @DisplayName("should return empty when ID is null")
    void findById_returnsEmpty_whenIdIsNull() {
      Optional<Plan> foundPlan = planRepository.findById(null);

      assertFalse(foundPlan.isPresent());
    }

    @Test
    @DisplayName("should return updated plan data")
    void findById_returnsUpdatedData_afterUpdate() {
      Plan plan = createPlan(null, "Original Name");
      Plan savedPlan = planRepository.save(plan);

      savedPlan.setName("Updated Name");
      planRepository.save(savedPlan);

      Optional<Plan> foundPlan = planRepository.findById(savedPlan.getId());

      assertTrue(foundPlan.isPresent());
      assertEquals("Updated Name", foundPlan.get().getName());
    }
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return empty list when no plans")
    void findAll_returnsEmptyList_whenNoPlans() {
      List<Plan> plans = planRepository.findAll();

      assertNotNull(plans);
      assertTrue(plans.isEmpty());
    }

    @Test
    @DisplayName("should return all saved plans")
    void findAll_returnsAllSavedPlans() {
      planRepository.save(createPlan(null, "Plan 1"));
      planRepository.save(createPlan(null, "Plan 2"));
      planRepository.save(createPlan(null, "Plan 3"));

      List<Plan> plans = planRepository.findAll();

      assertEquals(3, plans.size());
    }

    @Test
    @DisplayName("should return unmodifiable list")
    void findAll_returnsUnmodifiableList() {
      planRepository.save(createPlan(null, "Plan 1"));
      List<Plan> plans = planRepository.findAll();

      assertThrows(UnsupportedOperationException.class, () -> {
        plans.add(createPlan(null, "Plan 2"));
      });
    }

    @Test
    @DisplayName("should maintain insertion order")
    void findAll_maintainsInsertionOrder() {
      planRepository.save(createPlan(null, "First Plan"));
      planRepository.save(createPlan(null, "Second Plan"));
      planRepository.save(createPlan(null, "Third Plan"));

      List<Plan> plans = planRepository.findAll();

      assertEquals("First Plan", plans.get(0).getName());
      assertEquals("Second Plan", plans.get(1).getName());
      assertEquals("Third Plan", plans.get(2).getName());
    }

    @Test
    @DisplayName("should not include deleted plans")
    void findAll_doesNotIncludeDeletedPlans() {
      Plan plan1 = planRepository.save(createPlan(null, "Plan 1"));
      Plan plan2 = planRepository.save(createPlan(null, "Plan 2"));
      planRepository.save(createPlan(null, "Plan 3"));

      planRepository.deleteById(plan1.getId());
      planRepository.deleteById(plan2.getId());

      List<Plan> plans = planRepository.findAll();

      assertEquals(1, plans.size());
      assertEquals("Plan 3", plans.get(0).getName());
    }

    @Test
    @DisplayName("should return defensive copy")
    void findAll_returnsDefensiveCopy() {
      Plan plan = planRepository.save(createPlan(null, "Original Plan"));
      List<Plan> plans = planRepository.findAll();

      plans.get(0).setName("Modified from list");

      Optional<Plan> originalPlan = planRepository.findById(plan.getId());
      assertEquals("Original Plan", originalPlan.get().getName());
    }

  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("should delete existing plan and return true")
    void deleteById_deletesExistingPlan_andReturnsTrue() {
      Plan plan = planRepository.save(createPlan(null, "Plan to delete"));

      boolean result = planRepository.deleteById(plan.getId());

      assertTrue(result);
      assertFalse(planRepository.findById(plan.getId()).isPresent());
    }

    @Test
    @DisplayName("should return false when plan does not exist")
    void deleteById_returnsFalse_whenPlanDoesNotExist() {
      boolean result = planRepository.deleteById(999);

      assertFalse(result);
    }

    @Test
    @DisplayName("should return false when ID is null")
    void deleteById_returnsFalse_whenIdIsNull() {
      boolean result = planRepository.deleteById(null);

      assertFalse(result);
    }

    @Test
    @DisplayName("should not affect other plans")
    void deleteById_doesNotAffectOtherPlans() {
      Plan plan1 = planRepository.save(createPlan(null, "Plan 1"));
      Plan plan2 = planRepository.save(createPlan(null, "Plan 2"));
      Plan plan3 = planRepository.save(createPlan(null, "Plan 3"));

      planRepository.deleteById(plan2.getId());

      assertTrue(planRepository.findById(plan1.getId()).isPresent());
      assertFalse(planRepository.findById(plan2.getId()).isPresent());
      assertTrue(planRepository.findById(plan3.getId()).isPresent());
    }

    @Test
    @DisplayName("should allow re-saving after deletion")
    void deleteById_allowsReSaving_afterDeletion() {
      Plan plan = createPlan(null, "Plan to delete");
      Plan savedPlan = planRepository.save(plan);
      Integer originalId = savedPlan.getId();

      planRepository.deleteById(originalId);

      Plan newPlan = createPlan(null, "New Plan");
      Plan reSavedPlan = planRepository.save(newPlan);

      assertNotEquals(originalId, reSavedPlan.getId());
    }
  }

  @Nested
  @DisplayName("Thread Safety")
  class ThreadSafetyTests {

    @Test
    @DisplayName("should handle concurrent saves without ID conflicts")
    void save_handlesConcurrentSaves_withoutIdConflicts() throws InterruptedException {
      final int threadCount = 100;
      Thread[] threads = new Thread[threadCount];

      for (int i = 0; i < threadCount; i++) {
        final int index = i;
        threads[i] = new Thread(() -> {
          Plan plan = createPlan(null, "Plan " + index);
          planRepository.save(plan);
        });
        threads[i].start();
      }

      for (Thread thread : threads) {
        thread.join();
      }

      List<Plan> plans = planRepository.findAll();
      assertEquals(threadCount, plans.size());

      // Check all IDs are unique
      long uniqueIds = plans.stream()
          .map(Plan::getId)
          .distinct()
          .count();
      assertEquals(threadCount, uniqueIds);
    }

    @Test
    @DisplayName("should handle concurrent deletes safely")
    void deleteById_handlesConcurrentDeletes_safely() throws InterruptedException {
      // Create 10 plans
      for (int i = 0; i < 10; i++) {
        planRepository.save(createPlan(null, "Plan " + i));
      }

      final int threadCount = 10;
      Thread[] threads = new Thread[threadCount];

      for (int i = 0; i < threadCount; i++) {
        final int planId = i + 1;
        threads[i] = new Thread(() -> {
          planRepository.deleteById(planId);
        });
        threads[i].start();
      }

      for (Thread thread : threads) {
        thread.join();
      }

      List<Plan> remainingPlans = planRepository.findAll();
      assertEquals(0, remainingPlans.size());
    }
  }

  // Helper method to create a plan
  private Plan createPlan(Integer id, String name) {
    Plan plan = new Plan();
    plan.setId(id);
    plan.setName(name);
    plan.setType(PlanType.NORMAL);
    plan.setTotalAdults(2);
    plan.setTotalKids(1);
    plan.setOrigin(madrid);
    plan.setDestination(barcelona);
    return plan;
  }
}
