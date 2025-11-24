package io.github.creature.herder.building;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class FoodDispenser extends RenderableObject {
  public static final int FOODS_PER_DISPLAY_SLOT = 3;
  int maxFood;
  List<Food> foods;
  List<Vector2> foodWorldCoords;
  boolean flipped;

  public FoodDispenser(final int x, final int y, final boolean flipped) {
    final Texture texture = new Texture("foodDispenser.png");
    renderable =
        new Renderable(
            texture,
            new Vector2(x, y),
            new Vector2(1f, 1f),
            new Vector2(.5f, .25f),
            1f,
            flipped,
            3f);
    this.flipped = flipped;
    this.foods = new ArrayList<>();
    foodWorldCoords = createFoodDisplayPositions();
    this.maxFood = FOODS_PER_DISPLAY_SLOT * foodWorldCoords.size();
  }

  public List<Renderable> getRenderables() {
    final List<Renderable> renderables = new ArrayList<>();
    renderables.add(this.renderable);

    int maxFoods = Math.min(foodWorldCoords.size(), foods.size() / FOODS_PER_DISPLAY_SLOT);
    for (int i = 0; i < maxFoods; i++) {
      renderables.add(
          new Renderable(
              foods.get(i * 3).getTexture(),
              foodWorldCoords.get(i),
              new Vector2(.1f, .1f),
              new Vector2(.5f, .5f),
              5f));
    }
    return renderables;
  }

  private List<Vector2> createFoodDisplayPositions() {
    float spriteWidth = this.renderable.size.x;
    final float x = this.renderable.getWorldCoord().x;
    final float y = this.renderable.getWorldCoord().y;

    final List<Vector2> displayPositions = new ArrayList<>();
    float delta = spriteWidth / 6f;

    for (int z = 0; z < 3; z++) {
      for (float i = 0; i < 4; i++) {
        for (float j = 0; j < 4; j++) {

          float offsetX = (i + 2f + j - z / 4f - 2f) * delta;
          float offsetY = (j + z / 4f - 2f) * delta;
          displayPositions.add(
              new Vector2(x + (flipped ? offsetY : offsetX), y + (flipped ? offsetX : offsetY)));
        }
      }
    }
    return displayPositions.reversed();
  }

  public boolean addFood(Food food) {
    if (foods.size() < maxFood) {
      foods.add(food);
      return true;
    }
    return false;
  }
}
