package io.github.creature.herder.input;

import static io.github.creature.herder.screen.BuildingScreen.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.building.FoodDispenser;
import io.github.creature.herder.building.Pen;
import io.github.creature.herder.building.TrashContainer;
import io.github.creature.herder.camera.WorldCamera;
import io.github.creature.herder.entity.EntityState;
import io.github.creature.herder.entity.creatures.Creature;
import io.github.creature.herder.entity.customer.Customer;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.food.FoodPoop;
import io.github.creature.herder.items.FoodBag;
import io.github.creature.herder.render.ClosestObjectWithDistance;
import io.github.creature.herder.render.RenderableObject;
import io.github.creature.herder.screen.ui.UIStomach;
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

    // if clicked creature
    if (justLeftClicked()) {
      other.stream().filter(UIStomach.class::isInstance).forEach(RenderableObject::dispose);
      Vector2 cameraWorldPosition =
          CoordUtil.ScreenToWorld(
              new Vector2(
                  Gdx.input.getX() - Gdx.graphics.getWidth() / 2f,
                  Gdx.graphics.getHeight() / 2f - Gdx.input.getY()));
      Optional<ClosestObjectWithDistance> closestCreature =
          getClosestRenderableObject(new ArrayList<>(creatures), cameraWorldPosition, true);

      if (closestCreature.isPresent()
          && closestCreature.get().distance() < 1f
          && closestCreature.get().object() instanceof Creature creature) {
        other.add(new UIStomach(creature));
      }
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
      pickable.addAll(building.getAllDispensers());
      Optional<ClosestObjectWithDistance> closestRenderableObject =
          getClosestRenderableObject(pickable, player.getRenderable().getWorldCoord(), false);
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
          int foodsInDump = dp.getAllocatedFoods();
          if (!foodBag.foods.isEmpty() && foodsInDump < dp.getMaxFood()) {
            Food food = foodBag.foods.getLast();
            FoodPoop poop =
                new FoodPoop(
                    foodBag.getRenderable().getWorldCoord(),
                    food,
                    dp,
                    false,
                    true,
                    Optional.of(dp.getFoodWorldCoord(foodsInDump)));
            poop.speed = 3f;
            other.add(poop);
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
    if (pen.isPresent()) {
      creature.putInPen(pen.get());
      player.pickedUpObject = null;
    } else {
      Optional<ClosestObjectWithDistance> closestCustomer =
          getClosestRenderableObject(
              new ArrayList<>(customers),
              player.pickedUpObject.getRenderable().getWorldCoord(),
              false);
      closestCustomer
          .filter(c -> c.distance() < 1f)
          .ifPresent(
              customer -> {
                if (((Customer) customer.object()).buy(creature)) {
                  player.pickedUpObject = null;
                }
              });
    }
  }

  private static Optional<ClosestObjectWithDistance> getClosestRenderableObject(
      List<RenderableObject> renderables, Vector2 worldCoord, boolean useCenter) {
    if (renderables.isEmpty()) {
      return Optional.empty();
    }
    return renderables.stream()
        .min(
            Comparator.comparingDouble(
                c ->
                    (useCenter
                            ? c.getRenderable().getWorldCoord(new Vector2(.5f, .5f))
                            : c.getRenderable().getWorldCoord())
                        .dst(worldCoord)))
        .map(
            c ->
                new ClosestObjectWithDistance(
                    c,
                    (useCenter
                            ? c.getRenderable().getWorldCoord(new Vector2(.5f, .5f))
                            : c.getRenderable().getWorldCoord())
                        .dst(worldCoord)));
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
      } else if (closestObjectWithDistance.object() instanceof TrashContainer trashContainer) {
        trashContainer.flush();
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

  public static boolean justLeftClicked() {
    return Gdx.input.justTouched() && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
  }
}
