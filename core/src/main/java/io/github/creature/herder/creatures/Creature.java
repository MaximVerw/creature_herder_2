package io.github.creature.herder.creatures;

import static io.github.creature.herder.player.Player.computePickedUpWorldCoord;
import static io.github.creature.herder.screen.BuildingScreen.creatures;
import static io.github.creature.herder.screen.BuildingScreen.player;
import static io.github.creature.herder.util.RandomUtil.RANDOM;

import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.building.Pen;
import io.github.creature.herder.food.DigestionTrack;
import io.github.creature.herder.food.Food;
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
    this.size = Math.clamp(size, .7f, 1.5f);
    this.renderable.size = new Vector2(this.size, this.size);
    this.renderable.woordCoord = pen.getWorldCoord().cpy();
    this.speed = this.getSpeed() / this.speed;
    this.stomach = new Stomach(size, digestionSpeed, digestionTracks);
    this.health = 10;
    goGetFood();
  }

  @Override
  public void update(final float delta) {
    processFood();
    if (state.getState().equals(EntityState.State.IDLE)) {
      if (pen != null && RANDOM.nextFloat() < 0.01f) {
        walkToRandomTile();
      }
    } else if (state.getState().equals(EntityState.State.WALKING)) {
      final Vector2 creatureWorldCoord = renderable.getWorldCoord();
      if (creatureWorldCoord.dst2(state.getTargetWorldCoord()) < .05f) {
        if (creatureWorldCoord.dst2(pen.getDispenserPosition()) < .11f) {
          this.stomach.takeFood(this.pen.getDispenser());
        }
        state.idle();
      } else {
        final Vector2 deltaWorld =
            state.getTargetWorldCoord().cpy().sub(renderable.getWorldCoord());
        Vector2 clampedDelta = deltaWorld.nor().scl(delta * speed);
        renderable.woordCoord = renderable.getWorldCoord().add(clampedDelta);
      }
    } else if (state.getState().equals(EntityState.State.PICKED_UP)) {
      state.setDirection(player.getState().getDirection());
      renderable.woordCoord = computePickedUpWorldCoord();
    } else if (state.getState().equals(EntityState.State.DEAD)) {
      renderable.size = renderable.size.scl(.95f);
    }
    checkHealth();
  }

  private void processFood() {
    if (state.getState().equals(EntityState.State.IDLE) && pen != null && stomach.foodCheck()) {
      if (!stomach.canEat()) {
        if (!stomach.food.isEmpty()) {
          poop(stomach.food.getFirst());
        } else {
          int n = 0;
        }
        takeDamage();
        goGetFood();
      } else {
        final Optional<Food> poopOpt = stomach.processFood();
        poopOpt.ifPresent(this::poop);
        if (!stomach.canEat()) {
          goGetFood();
        }
        reproduce();
      }
    }
  }

  private boolean poop(Food poop) {
    return pen.getDump().addFood(poop);
  }

  private void reproduce() {
    if (RANDOM.nextFloat() < 0.01f) {
      Creature child = createCreature(getType(), pen, size, stomach.digestionSpeed);
      child.getRenderable().woordCoord = renderable.getWorldCoord().cpy();
      creatures.add(child);
    }
  }

  public static Creature createCreature(
      CreatureType type, Pen pen, float size, float digestionSpeed) {
    return switch (type) {
      case RAT -> new Rat(pen, size, digestionSpeed);
    };
  }

  private void walkToRandomTile() {
    final float tileI =
        Math.abs(RANDOM.nextInt()) % (pen.getSize() - 2) + 1 + pen.getWorldCoord().x;
    final float tileJ =
        Math.abs(RANDOM.nextInt()) % (pen.getSize() - 2) + 1 + pen.getWorldCoord().y;
    state.walkTowardsWorldCoord(
        new Vector2(
            tileI + .5f * (RANDOM.nextFloat() - .5f), tileJ + .5f * (RANDOM.nextFloat() - .5f)),
        renderable.getWorldCoord());
  }

  private void goGetFood() {
    if (IDLE_STATES.contains(state.getState())
        && !this.pen.getDispenser().getFoods().isEmpty()
        && !this.stomach.isFull()) {
      final Vector2 dispenserPositionWorldCoord = pen.getDispenserPosition();
      this.state.walkTowardsWorldCoord(dispenserPositionWorldCoord, renderable.getWorldCoord());
    }
  }

  public boolean removeMe() {
    return state.getState().equals(EntityState.State.DEAD) && (renderable.size.x < .1f);
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

  abstract CreatureType getType();
}
