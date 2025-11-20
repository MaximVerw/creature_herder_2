package io.github.creature.herder.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class WorldCamera {
  public static OrthographicCamera camera = new OrthographicCamera();
  public static Vector2 position = new Vector2();

  private WorldCamera() {}

  public static void resizeCamera() {
    final float width = Gdx.graphics.getWidth();
    final float height = Gdx.graphics.getHeight();
    camera.setToOrtho(false, width, height);

    updateCamera();
  }

  public static void updateCamera() {
    camera.position.set(
        position.x * Gdx.graphics.getWidth(), position.y * Gdx.graphics.getHeight(), 0f);
    camera.direction.set(new Vector3(0f, 0f, -1f)).nor();
    camera.up.set(Vector3.Y);
    camera.update();
  }

  public static void resetCamera() {
    position.set(0f, 0f);
    updateCamera();
  }

  public static Vector2 toScreen(Vector2 worldCoord) {
    float x = (worldCoord.x + .5f) * Gdx.graphics.getWidth();
    float y = (worldCoord.y + .5f) * Gdx.graphics.getHeight();

    return new Vector2(x - WorldCamera.position.x, y - WorldCamera.position.y);
  }

  public static Vector2 toWorld(Vector2 screenCoord) {
    float x = screenCoord.x + WorldCamera.position.x;
    float y = screenCoord.y + WorldCamera.position.y;
    return new Vector2(x / Gdx.graphics.getWidth() - .5f, y / Gdx.graphics.getHeight() - .5f);
  }
}
