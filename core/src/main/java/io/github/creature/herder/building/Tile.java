package io.github.creature.herder.building;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Tile extends RenderableObject {
  private static final Map<TileType, Texture> TEXTURE_MAP = createTextures();

  TileType type;

  public Tile(final TileType type, final int x, final int y) {
    final Sprite sprite = new Sprite(TEXTURE_MAP.get(type));
    Vector2 size = new Vector2(1f, .499f);
    Vector2 worldCoord = new Vector2(x, y);

    this.type = type;
    this.renderable = new Renderable(sprite, worldCoord, size, new Vector2(.5f, .5f), 1f);
  }

  private static String getPathForType(final TileType type) {
    if (type.equals(TileType.Background)) {
      return "floor.png";
    } else if (type.equals(TileType.Pen)) {
      return "grass.png";
    } else {
      throw new RuntimeException();
    }
  }

  private static Map<TileType, Texture> createTextures() {
    return Arrays.stream(TileType.values())
        .collect(Collectors.toMap(x -> x, x -> new Texture(getPathForType(x))));
  }
}
