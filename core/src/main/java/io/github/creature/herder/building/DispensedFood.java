package io.github.creature.herder.building;

import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.food.Food;

public record DispensedFood(Food food, Vector2 worldCoord) {}
