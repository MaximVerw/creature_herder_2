package io.github.creature.herder.food;

import io.github.creature.herder.creatures.Creature;
import io.github.creature.herder.creatures.Rat;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class DigestionTrack {
  public Optional<Food> digest(final Collection<Food> foods, final Creature creature) {
    if (!canDigest(foods)) {
      throw new RuntimeException("ELA, dat is niet eetbaar!");
    }
    final Collection<Food> requirements = getRequirements();

    for (final Food requirement : requirements) {
      if (requirement.equals(Food.ROTTEN) && !(creature instanceof Rat)) {
        creature.takeDamage();
      }
      foods.remove(requirement);
    }

    return getPoop();
  }

  public boolean canDigest(final Collection<Food> foods) {
    final Collection<Food> requirements = getRequirements();
    final Map<Food, Long> foodFrequencies = getFoodFrequencies(foods);
    final Map<Food, Long> requirementFrequencies = getFoodFrequencies(requirements);
    return requirementFrequencies.entrySet().stream()
        .noneMatch(e -> foodFrequencies.getOrDefault(e.getKey(), 0L) < e.getValue());
  }

  abstract Collection<Food> getRequirements();

  abstract Optional<Food> getPoop();

  private Map<Food, Long> getFoodFrequencies(final Collection<Food> foods) {
    return foods.stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
  }
}
