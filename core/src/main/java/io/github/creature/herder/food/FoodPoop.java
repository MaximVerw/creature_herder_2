package io.github.creature.herder.food;

import static io.github.creature.herder.food.Food.FOOD_SIZE;
import static io.github.creature.herder.util.LerpUtil.softLerp;

import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.building.FoodDispenser;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;

public class FoodPoop extends RenderableObject {
  public static final float START_SIZE = FOOD_SIZE / 5f;
  boolean stage1 = true;
  FoodDispenser target;
  Vector2 targetCoord;
  Food food;

  public FoodPoop(Vector2 worldCoord, Food food, FoodDispenser dump, boolean withOffset) {
    renderable =
        new Renderable(
            food.getTexture(),
            worldCoord,
            new Vector2(START_SIZE, START_SIZE),
            new Vector2(.5f, .5f),
            6f);
    this.food = food;
    target = dump;
    targetCoord = worldCoord.cpy().add(.6f, .6f);
    if (withOffset) {
      renderable.getWorldCoord().add(-.11f, .11f);
      targetCoord.add(-.16f, .16f);
    }
  }

  @Override
  public void update(float delta) {

    if (stage1) {
      if (targetCoord.dst(renderable.getWorldCoord()) > 0.005f) {
        renderable.setWoordCoord(softLerp(renderable.getWorldCoord(), targetCoord, delta, 0.005f));
        renderable.size =
            new Vector2(
                Math.min(renderable.size.x + delta, FOOD_SIZE),
                Math.min(renderable.size.y + delta, FOOD_SIZE));
      } else {
        stage1 = false;
        renderable.size = new Vector2(FOOD_SIZE, FOOD_SIZE);
      }
    } else {
      Vector2 dumpCoord = target.getFoodWorldCoord(target.getFoods().size());
      if (dumpCoord.dst(renderable.getWorldCoord()) > 0.01f) {
        renderable.setWoordCoord(
            softLerp(renderable.getWorldCoord(), dumpCoord, delta * 2f, 0.01f));
      } else {
        if (!this.isDisposed) {
          target.addFood(food);
        }
        this.isDisposed = true;
      }
    }
  }
}
