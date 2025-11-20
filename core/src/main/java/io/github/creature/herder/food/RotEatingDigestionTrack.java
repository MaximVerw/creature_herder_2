package io.github.creature.herder.food;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RotEatingDigestionTrack extends DigestionTrack {

  @Override
  Collection<Food> getRequirements() {
    return List.of(Food.ROTTEN, Food.ROTTEN);
  }

  Optional<Food> getPoop() {
    return Optional.empty();
  }
}
