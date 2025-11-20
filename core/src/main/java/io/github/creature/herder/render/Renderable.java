package io.github.creature.herder.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.util.CoordUtil;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
public class Renderable {
  public Sprite sprite;
  public Vector2 anchor; // 0,0 = left-bottom
    public float priority;

  public Renderable(
      Texture texture,
      Vector2 screenCoord,
      Vector2 size,
      Vector2 anchor,
      float alpha,
      boolean flipX,
      float priority) {
    sprite = new Sprite(texture);
    sprite.setPosition(screenCoord.x, screenCoord.y);
    sprite.setSize(size.x, size.y);
    sprite.setAlpha(alpha);
    if (flipX) {
      sprite.flip(true, false);
    }
    this.anchor = anchor;
    this.priority = priority;
  }

  public Renderable(Texture texture, Vector2 screenCoord, Vector2 size, Vector2 anchor, float priority) {
    this(texture, screenCoord, size, anchor, 1f, false, priority);
  }

  public Renderable(TextureRegion region, Vector2 screenCoord, Vector2 size, Vector2 anchor, float priority) {
    sprite = new Sprite(region);
    sprite.setPosition(screenCoord.x, screenCoord.y);
    sprite.setSize(size.x / sprite.getWidth(), size.y / sprite.getHeight());
    this.anchor = anchor;
    this.priority = priority;
  }

  public Vector2 getScreenCoord() {
    return new Vector2(sprite.getX(), sprite.getY());
  }

  public Vector2 getWorldCoord() {
    return CoordUtil.ScreenToWorld(getScreenCoord());
  }
}
