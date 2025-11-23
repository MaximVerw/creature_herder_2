package io.github.creature.herder.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class WorldCamera {
  public static float ZOOM = 200f;
  public static OrthographicCamera camera = new OrthographicCamera();
  public static Vector2 position = new Vector2(); // position * ZOOM = screenCoord

  private WorldCamera() {}

  public static void resizeCamera() {
    final float width = Gdx.graphics.getWidth();
    final float height = Gdx.graphics.getHeight();
    camera.setToOrtho(false, width, height);

    updateCamera();
  }

  public static void updateCamera() {
    camera.position.set(0f, 0f, 0f);
    camera.direction.set(new Vector3(0f, 0f, -1f)).nor();
    camera.up.set(Vector3.Y);
    camera.update();
  }

  public static void resetCamera() {
    position.set(0f, 0f);
    updateCamera();
  }
}
