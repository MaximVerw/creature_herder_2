package io.github.creature.herder.food;

import com.badlogic.gdx.graphics.Texture;

public enum Food {
  ROTTEN,
  MEAT,
  STONE;

  public Texture getTexture() {
    return new Texture(this.name() + ".png");
  }
}
