package io.github.creature.herder.building;

import static io.github.creature.herder.building.TileType.Background;

import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.render.Renderable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public class Building {
  public static final int FLOOR_SIZE = 20;
  List<List<Tile>> floorTiles;
  List<Pen> pens;
  List<Store> stores;

  public Building() {
    floorTiles = new ArrayList<>();
    for (int i = 0; i < FLOOR_SIZE; i++) {
      floorTiles.add(new ArrayList<>());
      for (int j = 0; j < FLOOR_SIZE; j++) {
        floorTiles.get(i).add(new Tile(Background, i, j));
      }
    }
    pens = new ArrayList<>();
    pens.add(new Pen(5, 4, 4));
    pens.add(new Pen(5, 12, 4));
    pens.add(new Pen(6, 4, 12));

    stores = new ArrayList<>();
    stores.add(new Store(Map.of(Food.STONE, 1.), 2, 0, false));
    stores.add(new Store(Map.of(Food.MEAT, .3, Food.ROTTEN, .5, Food.STONE, .2), 4, 0, false));
  }

  public boolean isWalkable(final int i, final int j) {
    return pens.stream().noneMatch(pen -> pen.isInside(i, j))
        && !(i < 0 || j < 0 || i >= FLOOR_SIZE || j >= FLOOR_SIZE);
  }

  public List<Renderable> getRenderables() {
    final List<Renderable> renderables = new ArrayList<>();
    for (final List<Tile> tileList : floorTiles) {
      for (final Tile tile : tileList) {
        renderables.add(tile.getRenderable());
      }
    }
    for (final Pen pen : pens) {
      renderables.addAll(pen.getRenderables());
    }
    for (final Store store : stores) {
      renderables.addAll(store.getRenderables());
    }
    return renderables;
  }

  public Optional<Pen> isPen(final int i, final int j) {
    return pens.stream().filter(pen -> pen.isInside(i, j)).findAny();
  }

  public Optional<FoodDispenser> isDispenser(Vector2 worldCoord) {
    return Stream.concat(stores.stream(), pens.stream().flatMap(pen -> Stream.of(pen.getDispenser(), pen.getDump())))
        .filter(foodDispenser -> foodDispenser.getRenderable().woordCoord.dst(worldCoord) < 1f)
        .findAny();
  }
}
