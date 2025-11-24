package io.github.creature.herder.creatures;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import lombok.Getter;

@Getter
public abstract class Entity extends RenderableObject {
  public static final int SPRITE_WIDTH = 16;
  public static final int SPRITE_HEIGHT = 24;
  protected Texture texture;
  protected EntityState state;
  float speed;

  public Entity() {
    texture = new Texture(getTextureFileName());
    state = new EntityState();
    renderable =
        new Renderable(
            getRegion(state), new Vector2(), new Vector2(1f / 3f, .5f), new Vector2(.5f, 0f), 3f);
    speed = 2f;
  }

  @Override
  public Renderable getRenderable() {
    renderable.sprite = getRegion(state);
    return renderable;
  }

  protected TextureRegion getRegion(final EntityState state) {
    return new TextureRegion(
        texture,
        state.getDirection().ordinal() * SPRITE_WIDTH,
        SPRITE_HEIGHT * getAnimationOffset(state),
        SPRITE_WIDTH,
        SPRITE_HEIGHT);
  }

  protected int getAnimationOffset(final EntityState entityState) {
    if (entityState.getState().equals(EntityState.State.WALKING)) {
      final int i = ((int) (TimeUtils.millis() / 200)) % 4;
      if (i == 0) {
        return 1;
      }
      if (i == 2) {
        return 2;
      }
      return 0;
    }
    return 0;
  }

  protected abstract String getTextureFileName();

  public abstract void update(float delta);
}
