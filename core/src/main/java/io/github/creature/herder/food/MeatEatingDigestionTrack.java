package io.github.creature.herder.food;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class MeatEatingDigestionTrack extends DigestionTrack {

  @Override
  public Collection<Food> getRequirements() {
    return List.of(Food.MEAT, Food.MEAT);
  }

  @Override
  public Optional<Food> getPoop() {
    return Optional.of(Food.ROTTEN);
  }
}
