package io.github.creature.herder.building;

import static io.github.creature.herder.util.RandomUtil.RANDOM;

import io.github.creature.herder.food.Food;
import io.github.creature.herder.items.FoodBag;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;

@Getter
public class Store extends FoodDispenser {
  Map<Food, Double> foodMix;

  public Store(
      Map<Food, Double> foodMix, final int price, final int x, final int y, final boolean flipped) {
    super(x, y, flipped);
    this.foodMix = foodMix;
    sanity(foodMix);
    fillFood();
    this.price = price;
  }

  private void fillFood() {
    while (foods.size() < maxFood) {
      AtomicReference<Double> random = new AtomicReference<>(RANDOM.nextDouble());
      foodMix.forEach(
          (food, probability) -> {
            if (random.get() < probability) {
              addFood(food);
              random.set(Double.MAX_VALUE);
            } else {
              random.set(random.get() - probability);
            }
          });
    }
  }

  private void sanity(Map<Food, Double> foodMix) {
    if (foodMix.values().stream().mapToDouble(Double::doubleValue).sum() != 1.) {
      throw new RuntimeException();
    }
  }

  @Override
  public FoodBag pickUp() {
    FoodBag foodBag = super.pickUp();
    fillFood();
    return foodBag;
  }
}
