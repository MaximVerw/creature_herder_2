package io.github.creature.herder.food;

import static io.github.creature.herder.food.Food.FOOD_SIZE;

import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.emotes.YuckEmote;
import io.github.creature.herder.items.FoodBag;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;

import java.util.ArrayList;
import java.util.List;

public class EatenFood extends RenderableObject {
  RenderableObject target;
  Food food;
  Vector2 anchor;
  float progress;
  float totalDistance;
  Vector2 startCoord;

  public EatenFood(Vector2 worldCoord, Food food, RenderableObject eater, Vector2 anchor) {
    renderable =
        new Renderable(
            food.getTexture(),
            worldCoord,
            new Vector2(FOOD_SIZE, FOOD_SIZE),
            new Vector2(.5f, .5f),
            6f);
    this.food = food;
    this.anchor = anchor;
    this.target = eater;
    this.startCoord = renderable.getWorldCoord();
    this.totalDistance = target.renderable.getWorldCoord(anchor).dst(startCoord);
    float progress = 0f;
  }

  @Override
  public void update(float delta) {
    Vector2 eaterCoord = target.renderable.getWorldCoord(anchor).cpy();
    renderable.setWoordCoord(startCoord.lerp(eaterCoord, progress));
    progress = Math.min(1f, progress + delta / 2f);
    if (progress > .2f) {
      renderable.size.scl(.9f);
    }

    if (renderable.size.x < FOOD_SIZE / 5f) {
      this.isDisposed = true;
    }
  }
}
