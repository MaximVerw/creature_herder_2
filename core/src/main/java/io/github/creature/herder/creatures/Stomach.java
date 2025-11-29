package io.github.creature.herder.creatures;

import static io.github.creature.herder.util.RandomUtil.RANDOM;

import io.github.creature.herder.building.FoodDispenser;
import io.github.creature.herder.food.DigestionTrack;
import io.github.creature.herder.food.Food;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

@Getter
public class Stomach {
  public static final int DEFAULT_STOMACH_SIZE = 10;
  int stomachSize;
  List<DigestionTrack> digestionTracks;
  List<Food> food;
  float digestionSpeed;

  Stomach(
      final float size, final float digestionSpeed, final List<DigestionTrack> digestionTracks) {
    super();
    this.stomachSize = Math.round(DEFAULT_STOMACH_SIZE * size);
    this.digestionSpeed = digestionSpeed;
    this.digestionTracks = digestionTracks;
    this.food = new ArrayList<>();
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

  public boolean takeFood(final FoodDispenser dispenser) {
    boolean ate = false;
    while (food.size() < stomachSize && !dispenser.getFoods().isEmpty()) {
      food.add(dispenser.getFoods().getLast());
      dispenser.getFoods().removeLast();
      ate = true;
    }
    return ate;
  }

  public boolean isHungry() {
    return food.size() <= stomachSize / 2;
  }
}
