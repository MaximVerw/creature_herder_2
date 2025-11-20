package io.github.creature.herder.building;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.util.CoordUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Tile {
  TileType type;
  Renderable renderable;

  public Tile(final TileType type, final int x, final int y) {
    final Sprite sprite = new Sprite(new Texture(getPathForType(type)));
    final Vector2 screenCoord = CoordUtil.WorldToScreen(new Vector2(x, y));
    sprite.setSize(400f, 199f);
    sprite.setPosition(screenCoord.x, screenCoord.y);

    this.type = type;
    this.renderable = new Renderable(sprite, new Vector2(.5f, .5f), 1f);
  }

  private String getPathForType(final TileType type) {
    if (type.equals(TileType.Background)) {
      return "floor.png";
    } else if (type.equals(TileType.Pen)) {
      return "grass.png";
    } else {
      throw new RuntimeException();
    }
  }
}
