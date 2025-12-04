package io.github.creature.herder.entity.creatures;

import static io.github.creature.herder.entity.creatures.Creature.MOUTH_ANCHOR;
import static io.github.creature.herder.util.RandomUtil.RANDOM;

import io.github.creature.herder.building.DispensedFood;
import io.github.creature.herder.building.FoodDispenser;
import io.github.creature.herder.food.DigestionTrack;
import io.github.creature.herder.food.EatenFood;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.screen.BuildingScreen;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

@Getter
public class Stomach {
  public static final int DEFAULT_STOMACH_SIZE = 10;
  public static final int MINIMUM_STOMACH_SIZE = 5;
  int stomachSize;
  List<DigestionTrack> digestionTracks;
  List<Food> food;
  float digestionSpeed;

  Stomach(
      final float growth,
      final float stomachSize,
      final float digestionSpeed,
      final List<DigestionTrack> digestionTracks) {
    this.digestionSpeed = digestionSpeed;
    this.digestionTracks = digestionTracks;
    this.food = new ArrayList<>();
    updateStomachSize(stomachSize, growth);
  }

  public void updateStomachSize(float creatureSize, float creatureGrowth) {
    this.stomachSize =
        Math.max(
            MINIMUM_STOMACH_SIZE, Math.round(DEFAULT_STOMACH_SIZE * creatureSize * creatureGrowth));
  }

  public boolean foodCheck() {
    return RANDOM.nextFloat() < (.003f * digestionSpeed);
  }

  public Optional<Food> processFood() {
    final int digestionTrackIndex = RANDOM.nextInt(digestionTracks.size());
    for (int i = 0; i < digestionTracks.size(); i++) {
      final DigestionTrack digestionTrack =
          digestionTracks.get((digestionTrackIndex + i) % digestionTracks.size());
      if (digestionTrack.canDigest(food)) {
        return digestionTrack.digest(food);
      }
    }
    throw new RuntimeException("Stomach problems");
  }

  public boolean canEat() {
    return digestionTracks.stream().anyMatch(digestionTrack -> digestionTrack.canDigest(food));
  }

  public void takeFood(final FoodDispenser dispenser, Creature stomachOwner) {
    while (food.size() < stomachSize && !dispenser.getFoods().isEmpty()) {
      DispensedFood dispensed = dispenser.dispense();
      BuildingScreen.other.add(
          new EatenFood(dispensed.worldCoord(), dispensed.food(), stomachOwner, MOUTH_ANCHOR));
      this.food.add(dispensed.food());
    }
  }

  public boolean isHungry() {
    return food.size() <= stomachSize / 2;
  }

  public Optional<Food> getUselessFood() {
    return food.stream()
        .filter(f -> digestionTracks.stream().noneMatch(dt -> dt.getRequirements().contains(f)))
        .findAny();
  }
}
