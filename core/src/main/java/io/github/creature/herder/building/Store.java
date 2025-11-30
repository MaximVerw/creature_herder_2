package io.github.creature.herder.building;

import static io.github.creature.herder.util.RandomUtil.RANDOM;

import io.github.creature.herder.food.Food;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;

@Getter
public class Store extends FoodDispenser {
  Map<Food, Double> foodMix;

  public Store(
      Map<Food, Double> foodMix, final int price, final int x, final int y, final boolean flipped) {
    super(x, y, flipped);
    this.foodMix = new HashMap<>(foodMix);
    tweakProbabilities(this.foodMix);
    while (foods.size() < maxFood) {
      fillFood(0);
    }
    this.price = price;
  }

  private void fillFood(int index) {
    AtomicReference<Double> random = new AtomicReference<>(RANDOM.nextDouble());
    foodMix.forEach(
        (food, probability) -> {
          if (random.get() < probability) {
            insertFood(food, index);
            random.set(Double.MAX_VALUE);
          } else {
            random.set(random.get() - probability);
          }
        });
  }

  private void tweakProbabilities(Map<Food, Double> foodMix) {
    double sum = foodMix.values().stream().mapToDouble(d -> d).sum();
    Food sampleKey = foodMix.keySet().stream().findAny().orElseThrow();
    foodMix.compute(sampleKey, (k, v) -> v + 1. - sum);
    if (Math.abs(sum - 1.) > 0.01) {
      throw new RuntimeException("FoodMix does not make sense!, sum=" + sum);
    }
  }

  @Override
  public DispensedFood dispense() {
    if (foods.isEmpty()) {
      throw new RuntimeException();
    }
    int pickedFoodIndex = RANDOM.nextInt(foods.size());
    DispensedFood dispensedFood =
        new DispensedFood(foods.get(pickedFoodIndex), foodWorldCoords.get(pickedFoodIndex).cpy());
    foods.remove(pickedFoodIndex);
    fillFood(pickedFoodIndex);
    return dispensedFood;
  }
}
