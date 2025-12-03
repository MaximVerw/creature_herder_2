package io.github.creature.herder.building;

import io.github.creature.herder.screen.BuildingScreen;
import lombok.Getter;

@Getter
public class TrashContainer extends FoodDispenser {

  public static final int PRICE = 50;

  public TrashContainer(int x, int y, boolean flipped) {
    super(x, y, flipped);
  }

  public void flush() {
    if (foods.isEmpty()) {
      return;
    }
    if (BuildingScreen.player.getMoney() > PRICE) {
      foods.clear();
      BuildingScreen.player.setMoney(BuildingScreen.player.getMoney() - 50);
    }
  }
}
