package io.github.creature.herder.util;

import static io.github.creature.herder.camera.WorldCamera.ZOOM;

import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.camera.WorldCamera;

public class CoordUtil {

  public static Vector2 ScreenToWorld(Vector2 screenCoord) {
    return CameraToWorld(ScreenToCamera(screenCoord));
  }

  public static Vector2 WorldToScreen(Vector2 worldCoord) {
    return CameraToScreen(WorldToCamera(worldCoord));
  }

  public static Vector2 CameraToWorld(Vector2 cameraCoord) {
    final float i = 2f * cameraCoord.y - cameraCoord.x;
    final float j = 2f * cameraCoord.y + cameraCoord.x;

    return new Vector2(i, j);
  }

  public static Vector2 WorldToCamera(Vector2 worldCoord) {
    return new Vector2((worldCoord.y - worldCoord.x) / 2f, (worldCoord.y + worldCoord.x) / 4f);
  }

  public static Vector2 CameraToScreen(Vector2 cameraCoord) {
    return CameraToScreen(cameraCoord, WorldCamera.position);
  }

  public static Vector2 ScreenToCamera(Vector2 screenCoord) {
    return ScreenToCamera(screenCoord, WorldCamera.position);
  }

  public static Vector2 CameraToScreen(Vector2 cameraCoord, Vector2 cameraPosition) {
    float x = cameraCoord.x; // * Gdx.graphics.getWidth();
    float y = cameraCoord.y; // * Gdx.graphics.getHeight();

    return new Vector2((x - cameraPosition.x) * ZOOM, (y - cameraPosition.y) * ZOOM);
  }

  public static Vector2 ScreenToCamera(Vector2 screenCoord, Vector2 cameraPosition) {
    float x = screenCoord.x + cameraPosition.x;
    float y = screenCoord.y + cameraPosition.y;
    return new Vector2(x, y)
        .scl(1f / ZOOM); // new Vector2(x / Gdx.graphics.getWidth(), y / Gdx.graphics.getHeight());
  }
}
