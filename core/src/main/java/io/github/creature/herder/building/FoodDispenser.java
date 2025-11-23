package io.github.creature.herder.building;

import static io.github.creature.herder.util.RandomUtil.RANDOM;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.render.Renderable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class FoodDispenser {
  Renderable renderable;
  int maxFood;
  List<Food> foods;
  List<Vector2> foodWorldCoords;

  public FoodDispenser(final List<Food> foods, final int x, final int y) {
    final Texture texture = new Texture("foodDispenser.png");
    renderable =
        new Renderable(texture, new Vector2(x, y), new Vector2(1f, 1f), new Vector2(.5f, .25f), 3f);
    foodWorldCoords = createFoodDisplayPositions();
    this.maxFood = 3 * foodWorldCoords.size();
    this.foods = foods;
    for (int i = 0; i < maxFood; i++) {
      if (RANDOM.nextFloat() < .1f) {
        foods.add(Food.ROTTEN);
      } else if (RANDOM.nextFloat() < .1f) {
        foods.add(Food.STONE);
      } else {
        foods.add(Food.MEAT);
      }
    }
  }

  public List<Renderable> getRenderables() {
    final List<Renderable> renderables = new ArrayList<>();
    renderables.add(this.renderable);

    int maxFoods = Math.min(foodWorldCoords.size(), foods.size() / 3);
    for (int i = 0; i < maxFoods; i++) {
      renderables.add(
          new Renderable(
              foods.get(i * 3).getTexture(),
              foodWorldCoords.get(i),
              new Vector2(.1f, .1f),
              new Vector2(.5f, .5f),
              3f));
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
          displayPositions.add(
              new Vector2(x + (i + 2f + j - z / 4f - 2f) * delta, y + (j + z / 4f - 2f) * delta));
        }
      }
    }
    return displayPositions.reversed();
  }
}
