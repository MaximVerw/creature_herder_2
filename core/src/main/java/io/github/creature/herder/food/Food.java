package io.github.creature.herder.food;

import com.badlogic.gdx.graphics.Texture;
import java.util.Map;

public enum Food {
  ROTTEN,
  MEAT,
  STONE;

  private static Map<Food, Texture> TEXTURES_MAP = createTextures();

  public Texture getTexture() {
    return TEXTURES_MAP.get(this);
  }

  private static Map<Food, Texture> createTextures() {
    return Map.of(
        ROTTEN, createTexture(ROTTEN),
        MEAT, createTexture(MEAT),
        STONE, createTexture(STONE));
  }

  private static Texture createTexture(Food rotten) {
    return new Texture(rotten.name() + ".png");
  }
}
