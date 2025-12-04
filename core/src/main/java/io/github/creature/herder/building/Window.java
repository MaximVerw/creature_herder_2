package io.github.creature.herder.building;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import lombok.Getter;

@Getter
public class Window extends RenderableObject {
  public static final Texture WINDOW_TEXTURE = new Texture("images/window.png");
  boolean facingLeft;

  Window(final boolean facingLeft, final int x, final int y) {
    this.facingLeft = facingLeft;
    this.renderable =
        new Renderable(
            WINDOW_TEXTURE,
            new Vector2(x, y),
            new Vector2(1f, .5f),
            new Vector2(.5f, .5f),
            .75f,
            !isFacingLeft(),
            3f);
  }
}
