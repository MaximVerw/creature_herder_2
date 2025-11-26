package io.github.creature.herder.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.building.FoodDispenser;
import io.github.creature.herder.building.Store;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.player.Player;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import java.util.ArrayList;
import java.util.List;

import static io.github.creature.herder.screen.BuildingScreen.player;

public class FoodBag extends RenderableObject {
  public static int MAX_FOODS = 50;
  public List<Food> foods = new ArrayList<>();

  public FoodBag(FoodDispenser dispenser) {
      int price = dispenser.getPrice();
      for (int i = 0; i < MAX_FOODS; i++) {
        if (!dispenser.getFoods().isEmpty() && price <=player.getMoney()) {
        foods.add(dispenser.getFoods().getLast());
        dispenser.getFoods().removeLast();
        player.setMoney(player.getMoney()- price);
      }
    }
    final Texture texture = new Texture("llama.png");
    renderable =
        new Renderable(texture, new Vector2(), new Vector2(.5f, .5f), new Vector2(.3f, .05f), 5f);
  }

  public List<Renderable> getRenderables() {
    return List.of(renderable);
  }

  public void update(float delta) {
    renderable.woordCoord = Player.computePickedUpWorldCoord();
  }
}
