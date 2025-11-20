package io.github.creature.herder.building;

import static io.github.creature.herder.util.RandomUtil.RANDOM;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.util.CoordUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class FoodDispenser {
  Renderable renderable;
  int maxFood;
  List<Food> foods;
  List<Vector2> foodScreenCoords;

  public FoodDispenser(final List<Food> foods, final int x, final int y) {
    final Texture texture = new Texture("foodDispenser.png");
    renderable =
        new Renderable(
            texture,
            CoordUtil.WorldToScreen(new Vector2(x + 1, y)),
            new Vector2(1f, 1f),
            new Vector2(.5f, .125f), 3f);
    foodScreenCoords = createFoodDisplayPositions();
    this.maxFood = 3 * foodScreenCoords.size();
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

    for (int i = 0; i < Math.min(foodScreenCoords.size(), foods.size() / 3); i++) {
      renderables.add(
          new Renderable(
              foods.get(i * 3).getTexture(),
              foodScreenCoords.get(i),
              new Vector2(2f, 2f),
              new Vector2(.5f, .5f),
              3f));
    }
    return renderables;
  }

  private List<Vector2> createFoodDisplayPositions() {
    float spriteWidth = this.renderable.sprite.getWidth();
    float spriteHeight = this.renderable.sprite.getHeight();
    final float x = this.renderable.getScreenCoord().x + spriteWidth / 4f;
    final float y = this.renderable.getScreenCoord().y + spriteHeight / 2f;

    final List<Vector2> displayPositions = new ArrayList<>();
    for (int z = 0; z < 2; z++) {
      for (float i = -spriteWidth / 4f + .1f; i < spriteWidth / 4f; i += .1f) {
        for (float j = -spriteHeight / 4f; j < spriteHeight / 4f - .1f; j += .1f) {
          displayPositions.add(new Vector2(x + i + z * .02f - .01f, y + j - i / 2f - z * .01f));
        }
      }
    }
    return displayPositions;
  }
}
