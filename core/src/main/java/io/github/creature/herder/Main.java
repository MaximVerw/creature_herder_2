package io.github.creature.herder;

import com.badlogic.gdx.Game;
import io.github.creature.herder.screen.BuildingScreen;

public class Main extends Game {

  @Override
  public void create() {
    setScreen(new BuildingScreen());
  }
}
