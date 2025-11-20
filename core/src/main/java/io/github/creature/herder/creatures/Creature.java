package io.github.creature.herder.creatures;

import static io.github.creature.herder.util.RandomUtil.RANDOM;

import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.building.Pen;
import io.github.creature.herder.food.DigestionTrack;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.player.Player;
import io.github.creature.herder.util.CoordUtil;
import java.util.List;
import java.util.Optional;

public abstract class Creature extends Entity {
  public static List<EntityState.State> IDLE_STATES =
      List.of(EntityState.State.WALKING, EntityState.State.IDLE);
  Pen pen;
  float size;
  Stomach stomach;
  int health;

  Creature(
      final Pen pen,
      final float size,
      final float digestionSpeed,
      final List<DigestionTrack> digestionTracks) {
    super();
    this.pen = pen;
    pen.getCreatures().add(this);
    this.renderable.sprite.setSize(size, size);
    Vector2 screenCoord = CoordUtil.WorldToScreen(pen.getWorldCoord());
    this.renderable.sprite.setPosition(screenCoord.x, screenCoord.y);
    this.size = Math.clamp(size, .7f, 1.5f);
    this.speed = this.getSpeed() / this.speed;
    this.stomach = new Stomach(size, digestionSpeed, digestionTracks);
    this.health = 10;
  }

  @Override
  public void update(final float delta, final Player player) {
    if (!state.getState().equals(EntityState.State.DEAD) && stomach.foodCheck()) {
      if (!stomach.canEat()) {
        takeDamage();
        goGetFood();
      } else {
        final Optional<Food> poop = stomach.processFood(this);

        if (!stomach.canEat()) {
          goGetFood();
        }
        // TODO do something with poop
      }
    }
    if (state.getState().equals(EntityState.State.IDLE)) {
      if (pen != null && RANDOM.nextFloat() < 0.01f) {
        walkToRandomTile();
      }
    } else if (state.getState().equals(EntityState.State.WALKING)) {
      final Vector2 creatureWorldCoord = renderable.getWorldCoord();
      if (creatureWorldCoord.dst2(CoordUtil.ScreenToWorld(state.getTargetScreenCoord())) < .001f) {
        if (creatureWorldCoord.dst2(pen.getDispenserPosition()) < .0011f) {
          this.stomach.takeFood(this.pen.getDispenser());
          walkToRandomTile();
        } else {
          state.idle();
        }
      } else {
        final Vector2 deltaScreen =
            state.getTargetScreenCoord().cpy().sub(renderable.getScreenCoord());
        Vector2 clampedDelta = deltaScreen.scl(1f, 2f).nor().scl(delta * speed).scl(1f, .5f);
        renderable.sprite.translate(clampedDelta.x, clampedDelta.y);
      }
    } else if (state.getState().equals(EntityState.State.PICKED_UP)) {
      state.setDirection(player.getState().getDirection());
      final float offset = player.getRenderable().sprite.getHeight() / 2f;
      final Vector2 offsetVector =
          EntityState.directionVectors.get(player.getState().getDirection().ordinal());
      Vector2 newPosition =
          player.getRenderable().getScreenCoord().cpy().add(offsetVector.scl(offset, offset / 2f));
      renderable.sprite.setPosition(newPosition.x, newPosition.y);
    } else if (state.getState().equals(EntityState.State.DEAD)) {
      renderable.sprite.setSize(
          renderable.sprite.getWidth() * .95f, renderable.sprite.getHeight() * .95f);
    }
    checkHealth();
  }

  private void walkToRandomTile() {
    final float tileI =
        Math.abs(RANDOM.nextInt()) % (pen.getSize() - 2) + 1 + pen.getWorldCoord().x;
    final float tileJ =
        Math.abs(RANDOM.nextInt()) % (pen.getSize() - 2) + 1 + pen.getWorldCoord().y;
    final Vector2 screenCoord = CoordUtil.WorldToScreen(new Vector2(tileI, tileJ));

    state.walkTowardsScreenCoord(
        new Vector2(
            screenCoord.x + RANDOM.nextFloat() * 1.5f - .75f,
            screenCoord.y + RANDOM.nextFloat() * .76f - .38f),
        renderable.getScreenCoord());
  }

  private void goGetFood() {
    if (IDLE_STATES.contains(state.getState())
        && !this.pen.getDispenser().getFoods().isEmpty()
        && !this.stomach.isFull()) {
      final Vector2 dispenserPositionWorldCoord = pen.getDispenserPosition();
      this.state.walkTowardsScreenCoord(
          CoordUtil.WorldToScreen(dispenserPositionWorldCoord), renderable.getScreenCoord());
    }
  }

  public boolean removeMe() {
    return state.getState().equals(EntityState.State.DEAD) && (renderable.sprite.getWidth() < .1f);
  }

  private void checkHealth() {
    if (this.health < 0 && !this.state.getState().equals(EntityState.State.PICKED_UP)) {
      removeFromPen();
      this.state.die();
    }
  }

  public void pickUp() {
    removeFromPen();
    this.state.pickUp();
  }

  private void removeFromPen() {
    if (this.pen == null) {
      return;
    }
    this.pen.getCreatures().remove(this);
    this.pen = null;
  }

  public void putInPen(final Pen pen) {
    this.pen = pen;
    this.pen.getCreatures().add(this);
    this.state.idle();
  }

  public void takeDamage() {
    this.health -= 1;
  }
}
