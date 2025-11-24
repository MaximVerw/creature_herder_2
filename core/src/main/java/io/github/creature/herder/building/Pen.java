package io.github.creature.herder.building;

import static io.github.creature.herder.util.RandomUtil.RANDOM;

import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.creatures.Creature;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.render.Renderable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Pen {
  public static final int DISPENSER_X_OFFSET = 1;
  Vector2 worldCoord;
  int size;
  List<List<Tile>> penTiles;
  List<Window> windows;
  List<Creature> creatures;
  FoodDispenser dispenser;
  FoodDispenser dump;

  public Pen(final int size, final int x, final int y) {
    this.worldCoord = new Vector2(x, y);
    this.size = size;
    this.creatures = new ArrayList<>();
    this.dispenser = new FoodDispenser(new ArrayList<>(), x + DISPENSER_X_OFFSET, y, false);
    this.dump = new FoodDispenser(new ArrayList<>(), x, y + DISPENSER_X_OFFSET, true);
    fillFoods();

    penTiles = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      penTiles.add(new ArrayList<>());
      for (int j = 0; j < size; j++) {
        penTiles.get(i).add(new Tile(TileType.Pen, x + i, y + j));
      }
    }
    windows = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      if (i != DISPENSER_X_OFFSET) {
        windows.add(new Window(true, x + i, y));
      }
      windows.add(new Window(false, x, y + i));
      windows.add(new Window(true, x + i, y + size));
      windows.add(new Window(false, x + size, y + i));
    }
  }

  private void fillFoods() {
    for (int i = 0; i < dispenser.maxFood; i++) {
      if (RANDOM.nextFloat() < .3f) {
        dispenser.addFood(Food.ROTTEN);
      } else if (RANDOM.nextFloat() < .1f) {
        dispenser.addFood(Food.STONE);
      } else {
        dispenser.addFood(Food.MEAT);
      }
    }
  }

  public List<Renderable> getRenderables() {
    final List<Renderable> renderables = new ArrayList<>();
    for (final Window window : windows) {
      renderables.add(window.getRenderable());
    }
    renderables.addAll(this.dispenser.getRenderables());
    renderables.addAll(this.dump.getRenderables());

    for (final List<Tile> tileList : penTiles) {
      for (final Tile tile : tileList) {
        renderables.add(tile.getRenderable());
      }
    }
    return renderables;
  }

  public boolean isInside(final int i, final int j) {
    return i >= this.worldCoord.x
        && j >= this.worldCoord.y
        && i < (this.worldCoord.x + size)
        && j < (this.worldCoord.y + size);
  }

  public Vector2 getDispenserPosition() {
    return new Vector2(worldCoord.x + DISPENSER_X_OFFSET, worldCoord.y);
  }
}
