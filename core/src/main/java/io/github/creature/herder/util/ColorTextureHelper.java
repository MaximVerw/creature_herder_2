package io.github.creature.herder.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.Map;

public class ColorTextureHelper {
  static Map<String, Texture> textures = new HashMap<>();

  public static Texture getTexture(float scale, ColorType colorType) {
    Color color = ColorPaletteUtil.getColor(scale, colorType);
    return getTexture(color);
  }

  public static Texture getTexture(Color color) {
    String colorString = color.toString();
    if (!textures.containsKey(colorString)) {
      textures.put(colorString, getColorTexture(color));
    }
    return textures.get(colorString);
  }

  public static Texture getColorTexture(final Color color) {
    final Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(color);
    pixmap.fill();
    final Texture pixel = new Texture(pixmap);
    pixmap.dispose();
    return pixel;
  }
}
