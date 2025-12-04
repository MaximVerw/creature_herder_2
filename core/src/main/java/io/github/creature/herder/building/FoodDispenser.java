package io.github.creature.herder.building;

import static io.github.creature.herder.food.Food.FOOD_SIZE;
import static io.github.creature.herder.screen.BuildingScreen.other;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.food.FoodPoop;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class FoodDispenser extends RenderableObject {
  public static final Texture FOOD_DISPENSER_TEXTURE = new Texture("images/foodDispenser.png");
  int maxFood;
  List<Food> foods;
  List<Vector2> foodWorldCoords;
  boolean flipped;
  int price;

  public FoodDispenser(final int x, final int y, final boolean flipped) {
    renderable =
        new Renderable(
            FOOD_DISPENSER_TEXTURE,
            new Vector2(x, y),
            new Vector2(1f, 1f),
            new Vector2(.5f, .25f),
            1f,
            flipped,
            3f);
    this.flipped = flipped;
    this.foods = new ArrayList<>();
    foodWorldCoords = createFoodDisplayPositions();
    this.maxFood = foodWorldCoords.size();
    this.price = 0;
  }

  public List<Renderable> getRenderables() {
    final List<Renderable> renderables = new ArrayList<>();
    renderables.add(this.renderable);

    int maxFoods = Math.min(foodWorldCoords.size(), foods.size());
    for (int i = 0; i < maxFoods; i++) {
      renderables.add(
          new Renderable(
              foods.get(i).getTexture(),
              foodWorldCoords.get(i),
              new Vector2(FOOD_SIZE, FOOD_SIZE),
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

    for (int z = 0; z < 4; z++) {
      for (float i = 0; i < 4; i++) {
        for (float j = 0; j < 4; j++) {

          float offsetX = (i + 2f + j - z / 4f - 1.8f) * delta;
          float offsetY = (j + z / 4f - 2f) * delta;
          displayPositions.add(
              new Vector2(x + (flipped ? offsetY : offsetX), y + (flipped ? offsetX : offsetY)));
        }
      }
    }
    return displayPositions.reversed();
  }

  public boolean addFood(Food food) {
    return insertFood(food, foods.size());
  }

  protected boolean insertFood(Food food, int index) {
    if (foods.size() < maxFood && index >= 0 && index <= foods.size()) {
      foods.add(index, food);
      return true;
    }
    return false;
  }

  public Vector2 getFoodWorldCoord(int foodIndex) {
    return foodWorldCoords.get(Math.min(foodWorldCoords.size() - 1, foodIndex)).cpy();
  }

  public DispensedFood dispense() {
    DispensedFood dispensedFood =
        new DispensedFood(foods.getLast(), foodWorldCoords.get(foods.size() - 1).cpy());
    foods.removeLast();
    return dispensedFood;
  }

  public int getAllocatedFoods() {
    return (int)
        (other.stream()
                .filter(
                    o -> {
                      if (o instanceof FoodPoop poop) {
                        return poop.target.equals(this);
                      }
                      return false;
                    })
                .count()
            + foods.size());
  }
}
