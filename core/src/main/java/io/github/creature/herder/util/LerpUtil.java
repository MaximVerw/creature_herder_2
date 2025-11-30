package io.github.creature.herder.util;

import com.badlogic.gdx.math.Vector2;

public class LerpUtil {
  public static Vector2 softLerp(Vector2 start, Vector2 end, float delta, float minimumStep) {
    float adjustedDelta = Math.max(delta, minimumStep / end.dst(start));
    return start.lerp(end, adjustedDelta);
  }
}
