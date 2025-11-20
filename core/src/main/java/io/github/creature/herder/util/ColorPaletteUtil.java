package io.github.creature.herder.util;

import com.badlogic.gdx.graphics.Color;
import java.util.List;

public class ColorPaletteUtil {
  static final List<Color> blue =
      List.of(
          Color.valueOf("addada"),
          Color.valueOf("95cfcf"),
          Color.valueOf("7cc4c4"),
          Color.valueOf("63b9b9"),
          Color.valueOf("4cadad"),
          Color.valueOf("419494"),
          Color.valueOf("367b7b"),
          Color.valueOf("2b6262"),
          Color.valueOf("204a4a"),
          Color.valueOf("153131"));

  static final List<Color> red =
      List.of(
          Color.valueOf("f98f8f"),
          Color.valueOf("f86c6c"),
          Color.valueOf("f64a4a"),
          Color.valueOf("f42828"),
          Color.valueOf("ee0b0b"),
          Color.valueOf("cc0909"),
          Color.valueOf("aa0808"),
          Color.valueOf("880606"),
          Color.valueOf("660404"),
          Color.valueOf("440303"));
  static final List<Color> softGreen =
      List.of(
          Color.valueOf("c5d7b1"),
          Color.valueOf("b3cb99"),
          Color.valueOf("a1bf82"),
          Color.valueOf("90b36a"),
          Color.valueOf("7ea554"),
          Color.valueOf("6c8d48"),
          Color.valueOf("5a763c"),
          Color.valueOf("485e30"),
          Color.valueOf("364624"),
          Color.valueOf("242f18"));
  static final List<Color> green =
      List.of(
          Color.valueOf("a3e4a5"),
          Color.valueOf("88dc8a"),
          Color.valueOf("6cd46e"),
          Color.valueOf("50cc53"),
          Color.valueOf("37c23a"),
          Color.valueOf("2fa632"),
          Color.valueOf("278a2a"),
          Color.valueOf("1f6e21"),
          Color.valueOf("175319"),
          Color.valueOf("0f3710"));
  static final List<Color> brown =
      List.of(
          Color.valueOf("dbc5ac"),
          Color.valueOf("d1b393"),
          Color.valueOf("c6a27a"),
          Color.valueOf("bc9061"),
          Color.valueOf("af7e4a"),
          Color.valueOf("966c3f"),
          Color.valueOf("7d5a35"),
          Color.valueOf("64482a"),
          Color.valueOf("4b361f"),
          Color.valueOf("322415"));

  public static Color getColor(final float scale, final ColorType colorType) {
    final int i = Math.max((int) Math.floor(scale * 10f), 0);
    final int j = Math.min((int) Math.ceil(scale * 10f), 9);
    final Color color1 =
        switch (colorType) {
          case BLUE -> blue.get(i);
          case RED -> red.get(i);
          case SOFT_GREEN -> softGreen.get(i);
          case GREEN -> green.get(i);
          case BROWN -> brown.get(i);
        };
    final Color color2 =
        switch (colorType) {
          case BLUE -> blue.get(j);
          case RED -> red.get(j);
          case SOFT_GREEN -> softGreen.get(j);
          case GREEN -> green.get(j);
          case BROWN -> brown.get(j);
        };

    final float t = scale - i;
    return color1.cpy().lerp(color2.cpy(), t);
  }
}
