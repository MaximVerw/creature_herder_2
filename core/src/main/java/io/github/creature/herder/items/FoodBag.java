package io.github.creature.herder.items;

import static io.github.creature.herder.screen.BuildingScreen.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.building.FoodDispenser;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.player.Player;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import java.util.ArrayList;
import java.util.List;

public class FoodBag extends RenderableObject {
  public static final Texture FOOD_BAG_TEXTURE = new Texture("llama.png");
  public static int MAX_FOODS = 50;
  public List<Food> foods = new ArrayList<>();

  public FoodBag(FoodDispenser dispenser) {
    int price = dispenser.getPrice();
    for (int i = 0; i < MAX_FOODS; i++) {
      if (!dispenser.getFoods().isEmpty() && price <= player.getMoney()) {
        foods.add(dispenser.getFoods().getLast());
        dispenser.getFoods().removeLast();
        player.setMoney(player.getMoney() - price);
      }
    }
    renderable =
        new Renderable(
            FOOD_BAG_TEXTURE, new Vector2(), new Vector2(.5f, .5f), new Vector2(.3f, .05f), 5f);
  }

  public List<Renderable> getRenderables() {
    return List.of(renderable);
  }

  public void update(float delta) {
    renderable.woordCoord = Player.computePickedUpWorldCoord();
  }
}
