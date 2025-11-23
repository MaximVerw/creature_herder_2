package io.github.creature.herder.render;

import static io.github.creature.herder.camera.WorldCamera.ZOOM;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.util.CoordUtil;
import lombok.Setter;

@Setter
public class Renderable {
  public TextureRegion sprite;
  public Vector2 woordCoord;
  public boolean flipX;
  public Vector2 size;
  public Vector2 anchor; // 0,0 = left-bottom
  public float priority;

  public Renderable(
      Texture texture,
      Vector2 worldCoord,
      Vector2 size,
      Vector2 anchor,
      float alpha,
      boolean flipX,
      float priority) {
    Sprite sprite = new Sprite(texture);
    sprite.setAlpha(alpha);
    this.flipX = flipX;
    this.sprite = sprite;
    this.woordCoord = worldCoord;
    this.size = size;
    this.anchor = anchor;
    this.priority = priority;
  }

  public Renderable(
      Texture texture, Vector2 worldCoord, Vector2 size, Vector2 anchor, float priority) {
    this(texture, worldCoord, size, anchor, 1f, false, priority);
  }

  public Renderable(
      TextureRegion region, Vector2 worldCoord, Vector2 size, Vector2 anchor, float priority) {
    sprite = region;
    this.woordCoord = worldCoord;
    this.size = size;
    this.anchor = anchor;
    this.priority = priority;
  }

  public Vector2 getScreenCoord() {
    return CoordUtil.WorldToScreen(getWorldCoord());
  }

  public Vector2 getWorldCoord() {
    return woordCoord;
  }

  public float getScreenWidth() {
    return size.x * ZOOM;
  }

  public float getScreenHeight() {
    return size.y * ZOOM;
  }
}
