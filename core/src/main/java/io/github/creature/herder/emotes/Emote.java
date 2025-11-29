package io.github.creature.herder.emotes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;

public class Emote extends RenderableObject {
  public static final float EMOTE_SIZE = .2f;
  public static final float START_SIZE = EMOTE_SIZE / 5f;
  Vector2 targetCoord;

  public Emote(Vector2 worldCoord, Texture texture) {
    renderable =
        new Renderable(
            texture,
            worldCoord,
            new Vector2(START_SIZE, EMOTE_SIZE / 5f),
            new Vector2(.5f, .5f),
            6f);
    targetCoord = worldCoord.cpy().add(1f, 1f);
  }

  @Override
  public void update(float delta) {
    if (targetCoord.dst(renderable.getWorldCoord()) > 0.05f) {
      renderable.setWoordCoord(renderable.getWorldCoord().lerp(targetCoord, delta));
      renderable.size = renderable.size.add(delta / 2f, delta / 2f).clamp(0f, EMOTE_SIZE);
    } else {
      renderable.size.scl(.9f);
    }

    if (renderable.size.x < START_SIZE) {
      this.isDisposed = true;
    }
  }
}
