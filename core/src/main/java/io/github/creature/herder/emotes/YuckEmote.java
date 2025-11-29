package io.github.creature.herder.emotes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class YuckEmote extends Emote {
  public static final Texture YUCK_TEXTURE = new Texture("yuck.png");

  public YuckEmote(Vector2 worldCoord) {
    super(worldCoord, YUCK_TEXTURE);
  }
}
