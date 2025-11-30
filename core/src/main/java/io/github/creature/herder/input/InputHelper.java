package io.github.creature.herder.input;

import static io.github.creature.herder.screen.BuildingScreen.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.building.FoodDispenser;
import io.github.creature.herder.building.Pen;
import io.github.creature.herder.camera.WorldCamera;
import io.github.creature.herder.creatures.ClosestObjectWithDistance;
import io.github.creature.herder.creatures.Creature;
import io.github.creature.herder.creatures.EntityState;
import io.github.creature.herder.items.FoodBag;
import io.github.creature.herder.render.RenderableObject;
import io.github.creature.herder.util.CoordUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class InputHelper {

  private static final float WALKING_SPEED = 1f;

  public static void processInputs(final float delta) {
    if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
      throw new RuntimeException();
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

    boolean walkingSideways = false;
    if (isWalkingRight()) {
      walkingSideways = true;
      WorldCamera.position.add(
          CoordUtil.WorldToCamera(new Vector2(-1, 1).scl(WALKING_SPEED * delta)));
    }
    if (isWalkingLeft()) {
      walkingSideways = true;
      WorldCamera.position.add(
          CoordUtil.WorldToCamera(new Vector2(1, -1).scl(WALKING_SPEED * delta)));
    }
    if (isWalkingUp()) {
      WorldCamera.position.add(
          CoordUtil.WorldToCamera(
              new Vector2(1, 1).scl(WALKING_SPEED * delta * (walkingSideways ? 1f : 2f))));
    }
    if (isWalkingDown()) {
      WorldCamera.position.add(
          CoordUtil.WorldToCamera(
              new Vector2(-1, -1).scl(WALKING_SPEED * delta * (walkingSideways ? 1f : 2f))));
    }

    if (Gdx.input.isKeyPressed(Input.Keys.O)) {
      List<RenderableObject> pickable = new ArrayList<>(creatures);
      building
          .getPens()
          .forEach(
              pen -> {
                pickable.add(pen.getDispenser());
                pickable.add(pen.getDump());
              });
      pickable.addAll(building.getStores());
      Optional<ClosestObjectWithDistance> closestRenderableObject =
          getClosestRenderableObject(pickable, player.getRenderable().getWorldCoord());
      closestRenderableObject.ifPresent(InputHelper::pickUpObject);
    }
    if (Gdx.input.isKeyPressed(Input.Keys.P) && player.getPickedUpObject() != null) {
      if (player.getPickedUpObject() instanceof Creature creature) {
        sanityCheckPickedUpCreature(creature);
        dropPickedUpCreature(creature);
      } else if (player.getPickedUpObject() instanceof FoodBag foodBag) {
        dropFoodBag(foodBag);
      }
    }
  }

  private static void dropFoodBag(FoodBag foodBag) {
    Optional<FoodDispenser> dispenser =
        building.isDispenser(foodBag.getRenderable().getWorldCoord());
    dispenser.ifPresent(
        dp -> {
          while (!foodBag.foods.isEmpty() && dp.addFood(foodBag.foods.getLast())) {
            foodBag.foods.removeLast();
          }
          if (foodBag.foods.isEmpty()) {
            foodBag.isDisposed = true;
            player.pickedUpObject = null;
          }
        });
  }

  private static void dropPickedUpCreature(Creature creature) {
    final Vector2 pickedUpCreatureWorldCoord =
        player.getPickedUpObject().getRenderable().getWorldCoord();
    final Optional<Pen> pen =
        building.isPen(
            Math.round(pickedUpCreatureWorldCoord.x), Math.round(pickedUpCreatureWorldCoord.y));
    pen.ifPresent(
        value -> {
          creature.putInPen(value);
          player.pickedUpObject = null;
        });
  }

  private static Optional<ClosestObjectWithDistance> getClosestRenderableObject(
      List<RenderableObject> renderables, Vector2 worldCoord) {
    if (renderables.isEmpty()) {
      return Optional.empty();
    }
    return renderables.stream()
        .min(Comparator.comparingDouble(c -> c.getRenderable().getWorldCoord().dst(worldCoord)))
        .map(
            c ->
                new ClosestObjectWithDistance(
                    c, c.getRenderable().getWorldCoord().dst(worldCoord)));
  }

  private static void pickUpObject(ClosestObjectWithDistance closestObjectWithDistance) {
    if (closestObjectWithDistance.distance() > 1f) {
      return;
    }

    if (player.pickedUpObject == null) {
      if (closestObjectWithDistance.object() instanceof Creature creature) {
        if (Creature.IDLE_STATES.contains(creature.getState().getState())) {
          creature.pickUp();
          player.pickedUpObject = creature;
        }
      } else if (closestObjectWithDistance.object() instanceof FoodDispenser dispenser) {
        if (!dispenser.getFoods().isEmpty()) {
          FoodBag newBag = new FoodBag();
          player.pickedUpObject = newBag;
          other.add(newBag);
        }
      }
    } else if (player.pickedUpObject instanceof FoodBag foodBag) {
      if (closestObjectWithDistance.object() instanceof FoodDispenser dispenser) {
        foodBag.pickUp(dispenser);
      }
    }
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

  private static void sanityCheckPickedUpCreature(Creature creature) {
    if (!creature.getState().getState().equals(EntityState.State.PICKED_UP)) {
      throw new RuntimeException();
    }
  }
}
