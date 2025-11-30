package io.github.creature.herder.items;

import static io.github.creature.herder.screen.BuildingScreen.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.building.DispensedFood;
import io.github.creature.herder.building.FoodDispenser;
import io.github.creature.herder.food.EatenFood;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.player.Player;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import io.github.creature.herder.screen.BuildingScreen;
import java.util.ArrayList;
import java.util.List;

public class FoodBag extends RenderableObject {
  public static final Texture FOOD_BAG_TEXTURE = new Texture("llama.png");
  public static int MAX_FOODS = 100;
  public List<Food> foods = new ArrayList<>();

  public FoodBag() {
    renderable =
        new Renderable(
            FOOD_BAG_TEXTURE, new Vector2(), new Vector2(.5f, .5f), new Vector2(.3f, .05f), 5f);
  }

  public void pickUp(FoodDispenser dispenser) {
    int price = dispenser.getPrice();
    if (!dispenser.getFoods().isEmpty() && price <= player.getMoney() && foods.size() < MAX_FOODS) {
      DispensedFood dispensed = dispenser.dispense();
      foods.add(dispensed.food());
      player.setMoney(player.getMoney() - price);
      BuildingScreen.other.add(
          new EatenFood(dispensed.worldCoord(), dispensed.food(), this, renderable.anchor));
    }
  }

  @Override
  public void update(float delta) {
    renderable.woordCoord = Player.computePickedUpWorldCoord();
  }
}
