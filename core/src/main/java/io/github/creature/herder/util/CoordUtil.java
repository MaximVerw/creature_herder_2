package io.github.creature.herder.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.camera.WorldCamera;

public class CoordUtil {

    public static final float WORLD_SCREEN_SCALING_FACTOR = 400f;

    public static Vector2 ScreenToWorld(Vector2 screenCoord) {
    return CameraToWorld(ScreenToCamera(screenCoord));
  }

  public static Vector2 WorldToScreen(Vector2 worldCoord) {
    return CameraToScreen(WorldToCamera(worldCoord));
  }

  public static Vector2 CameraToWorld(Vector2 cameraCoord) {
    final float i = 2f * cameraCoord.y - cameraCoord.x;
    final float j = 2f * cameraCoord.y + cameraCoord.x;

    return new Vector2(Math.round(i/WORLD_SCREEN_SCALING_FACTOR), Math.round(j/ WORLD_SCREEN_SCALING_FACTOR));
  }

  public static Vector2 WorldToCamera(Vector2 worldCoord) {
    return new Vector2((worldCoord.y - worldCoord.x) / 2f, (worldCoord.y + worldCoord.x) / 4f).scl(WORLD_SCREEN_SCALING_FACTOR);
  }

  public static Vector2 CameraToScreen(Vector2 cameraCoord) {
    return CameraToScreen(cameraCoord, WorldCamera.position);
  }

  public static Vector2 CameraToScreen(Vector2 cameraCoord, Vector2 cameraPosition) {
    float x = cameraCoord.x;// * Gdx.graphics.getWidth();
    float y = cameraCoord.y;// * Gdx.graphics.getHeight();

    return new Vector2(x - cameraPosition.x, y - cameraPosition.y);
  }

  public static Vector2 ScreenToCamera(Vector2 screenCoord) {
    return ScreenToCamera(screenCoord, WorldCamera.position);
  }

  public static Vector2 ScreenToCamera(Vector2 screenCoord, Vector2 cameraPosition) {
    float x = screenCoord.x + cameraPosition.x;
    float y = screenCoord.y + cameraPosition.y;
    return new Vector2(x,y);//new Vector2(x / Gdx.graphics.getWidth(), y / Gdx.graphics.getHeight());
  }
}
