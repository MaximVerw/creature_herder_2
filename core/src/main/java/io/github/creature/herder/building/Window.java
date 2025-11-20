package io.github.creature.herder.building;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.util.CoordUtil;
import lombok.Getter;

@Getter
public class Window {
  boolean facingLeft;
  Renderable renderable;

  Window(final boolean facingLeft, final int x, final int y) {
    final Texture texture = new Texture("window.png");
    this.facingLeft = facingLeft;
    this.renderable =
        new Renderable(
            texture,
            CoordUtil.WorldToScreen(new Vector2(x, y)),
            new Vector2(400f, 200f),
            new Vector2(.5f, .5f),
            .75f,
            !isFacingLeft(), 5f);
  }
}
