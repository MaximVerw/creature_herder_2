package io.github.creature.herder.food;

import static io.github.creature.herder.food.Food.FOOD_SIZE;
import static io.github.creature.herder.util.LerpUtil.softLerp;

import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.building.FoodDispenser;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import java.util.Optional;

public class FoodPoop extends RenderableObject {
  public static final float START_SIZE = FOOD_SIZE / 5f;
  public boolean stage1 = true;
  public FoodDispenser target;
  Optional<Vector2> overrideHoming;
  Vector2 targetCoord;
  Food food;
  public float speed;

  public FoodPoop(
      Vector2 worldCoord,
      Food food,
      FoodDispenser dump,
      boolean withOffset,
      boolean skipStage1,
      Optional<Vector2> overrideHoming) {
    renderable =
        new Renderable(
            food.getTexture(),
            worldCoord,
            new Vector2(START_SIZE, START_SIZE),
            new Vector2(.5f, .5f),
            6f);
    this.food = food;
    this.speed = 1f;
    this.overrideHoming = overrideHoming;
    target = dump;
    targetCoord = worldCoord.cpy().add(.6f, .6f);
    if (withOffset) {
      renderable.getWorldCoord().add(-.11f, .11f);
      targetCoord.add(-.16f, .16f);
    }
    if (skipStage1) {
      renderable.setSize(new Vector2(FOOD_SIZE, FOOD_SIZE));
      stage1 = false;
    }
  }

  @Override
  public void update(float delta) {

    if (stage1) {
      if (targetCoord.dst(renderable.getWorldCoord()) > 0.005f * speed) {
        renderable.setWoordCoord(
            softLerp(renderable.getWorldCoord(), targetCoord, delta * speed, 0.005f * speed));
        renderable.size =
            new Vector2(
                Math.min(renderable.size.x + delta * speed, FOOD_SIZE),
                Math.min(renderable.size.y + delta * speed, FOOD_SIZE));
      } else {
        stage1 = false;
        renderable.size = new Vector2(FOOD_SIZE, FOOD_SIZE);
      }
    } else {
      Vector2 dumpCoord = overrideHoming.orElse(target.getFoodWorldCoord(target.getFoods().size()));
      if (dumpCoord.dst(renderable.getWorldCoord()) > 0.01f * speed) {
        renderable.setWoordCoord(
            softLerp(renderable.getWorldCoord(), dumpCoord, delta * 2f * speed, 0.01f * speed));
      } else {
        if (!this.isDisposed) {
          target.addFood(food);
        }
        this.isDisposed = true;
      }
    }
  }
}
