package io.github.creature.herder.input;

import static io.github.creature.herder.screen.BuildingScreen.*;
import static java.lang.System.exit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.building.Pen;
import io.github.creature.herder.camera.WorldCamera;
import io.github.creature.herder.creatures.ClosestCreatureWithDistance;
import io.github.creature.herder.creatures.Creature;
import io.github.creature.herder.creatures.EntityState;
import io.github.creature.herder.util.CoordUtil;
import java.util.Comparator;
import java.util.Optional;

public class InputHelper {

  private static final float WALKING_SPEED = 1f;

  public static void processInputs(final float delta) {
    if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
      Gdx.app.exit();
      exit(0);
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
      if (Gdx.graphics.isFullscreen()) {
        // If currently in fullscreen, switch to windowed mode
        Gdx.graphics.setWindowedMode(600, 400);
      } else {
        // If currently in windowed mode, switch to fullscreen
        // Get the current display mode (usually the primary monitor's resolution)
        final Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
        Gdx.graphics.setFullscreenMode(displayMode);
      }
    }

    if (Gdx.input.isKeyPressed(Input.Keys.T)) {
      WorldCamera.ZOOM *= 1.05f;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.G)) {
      WorldCamera.ZOOM /= 1.05f;
    }

    if (isWalkingRight()) {
      WorldCamera.position.add(
          CoordUtil.WorldToCamera(new Vector2(-1, 1).scl(WALKING_SPEED * delta)));
    }
    if (isWalkingLeft()) {
      WorldCamera.position.add(
          CoordUtil.WorldToCamera(new Vector2(1, -1).scl(WALKING_SPEED * delta)));
    }
    if (isWalkingUp()) {
      WorldCamera.position.add(
          CoordUtil.WorldToCamera(new Vector2(1, 1).scl(WALKING_SPEED * delta * 1.77f)));
    }
    if (isWalkingDown()) {
      WorldCamera.position.add(
          CoordUtil.WorldToCamera(new Vector2(-1, -1).scl(WALKING_SPEED * delta * 1.77f)));
    }

    if (Gdx.input.isKeyPressed(Input.Keys.O)) {
      if (player.getPickedUpCreature() == null) {
        pickUpClosestCreature();
      }
    }
    if (Gdx.input.isKeyPressed(Input.Keys.P) && player.getPickedUpCreature() != null) {
      if (player.getPickedUpCreature().getState().getState().equals(EntityState.State.PICKED_UP)) {
        dropPickedUpCreature();
      }
    }
  }

  private static void dropPickedUpCreature() {
    final Vector2 pickedUpCreatureWorldCoord =
        player.getPickedUpCreature().getRenderable().getWorldCoord();
    final Optional<Pen> pen =
        building.isPen(
            Math.round(pickedUpCreatureWorldCoord.x), Math.round(pickedUpCreatureWorldCoord.y));
    pen.ifPresent(value -> {
        player.getPickedUpCreature().putInPen(value);
        player.pickedUpCreature = null;
    });
  }

  private static void pickUpClosestCreature() {
    final Optional<ClosestCreatureWithDistance> closestCreatureOpt =
        getClosestCreature(player.getRenderable().getWorldCoord().cpy());
    closestCreatureOpt.ifPresent(
        closestCreature -> {
          if (Creature.IDLE_STATES.contains(closestCreature.closestCreature().getState().getState())
              && closestCreature.distance() < 1f) {
            closestCreature.closestCreature().pickUp();
            player.pickedUpCreature = closestCreature.closestCreature();
          }
        });
  }

  private static Optional<ClosestCreatureWithDistance> getClosestCreature(Vector2 worldCoord) {
    return creatures.stream()
        .min(Comparator.comparingDouble(c -> c.getRenderable().getWorldCoord().dst(worldCoord)))
        .map(
            c ->
                new ClosestCreatureWithDistance(
                    c, c.getRenderable().getWorldCoord().dst(worldCoord)));
  }

  public static boolean isWalkingDown() {
    return Gdx.input.isKeyPressed(Input.Keys.DOWN);
  }

  public static boolean isWalkingUp() {
    return Gdx.input.isKeyPressed(Input.Keys.UP);
  }

  public static boolean isWalkingLeft() {
    return Gdx.input.isKeyPressed(Input.Keys.LEFT);
  }

  public static boolean isWalkingRight() {
    return Gdx.input.isKeyPressed(Input.Keys.RIGHT);
  }
}
