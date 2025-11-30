package io.github.creature.herder.entity.creatures;

import static io.github.creature.herder.entity.player.Player.computePickedUpWorldCoord;
import static io.github.creature.herder.screen.BuildingScreen.creatures;
import static io.github.creature.herder.screen.BuildingScreen.player;
import static io.github.creature.herder.util.RandomUtil.RANDOM;

import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.building.FoodDispenser;
import io.github.creature.herder.building.Pen;
import io.github.creature.herder.emotes.YuckEmote;
import io.github.creature.herder.entity.Entity;
import io.github.creature.herder.entity.EntityState;
import io.github.creature.herder.food.DigestionTrack;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.food.FoodPoop;
import io.github.creature.herder.screen.BuildingScreen;
import java.util.List;
import java.util.Optional;

public abstract class Creature extends Entity {
  public static final Vector2 MOUTH_ANCHOR = new Vector2(.5f, .5f);
  public static List<EntityState.State> IDLE_STATES =
      List.of(EntityState.State.WALKING, EntityState.State.IDLE);
  Pen pen;
  public float size;
  public Stomach stomach;
  public int maxHealth;
  public int health;
  public float growth;

  Creature(
      final Pen pen,
      final float size,
      final float digestionSpeed,
      final int health,
      final List<DigestionTrack> digestionTracks,
      final boolean alreadyGrown) {
    super();
    this.growth = alreadyGrown ? 1f : 0.3f;
    this.pen = pen;
    pen.getCreatures().add(this);
    this.size = size;
    this.renderable.size = getSizeVector();
    this.renderable.woordCoord = pen.getWorldCoord().cpy();
    this.speed = this.getSpeed() / this.speed;
    this.stomach = new Stomach(growth, size, digestionSpeed, digestionTracks);
    this.health = health;
    this.maxHealth = this.health;
    goGetFood();
  }

  @Override
  public void update(final float delta) {
    processFood();
    if (state.getState().equals(EntityState.State.IDLE)) {
      if (pen != null && RANDOM.nextFloat() < 0.002f) {
        walkToRandomTile();
      }
    } else if (state.getState().equals(EntityState.State.WALKING)) {
      final Vector2 creatureWorldCoord = renderable.getWorldCoord();
      if (creatureWorldCoord.dst2(state.getTargetWorldCoord()) < .05f) {
        if (creatureWorldCoord.dst2(pen.getDispenserPosition()) < .11f) {
          this.stomach.takeFood(this.pen.getDispenser(), this);
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
      disposeCheck();
    }
    checkHealth();
  }

  private void processFood() {
    if (state.getState().equals(EntityState.State.IDLE) && pen != null && stomach.foodCheck()) {
        grow();
        if (!stomach.canEat()) {
        if (!stomach.food.isEmpty()) {
          Optional<Food> uselessFood = stomach.getUselessFood();
          if (uselessFood.isPresent()) {
            yuckEmote();
            poop(uselessFood.get(), true);
            stomach.food.remove(uselessFood.get());
          }
        }
        takeDamage();
        goGetFood();
      } else {
        this.renderable.size = getSizeVector();
        final Optional<Food> poopOpt = stomach.processFood();
        poopOpt.ifPresent(x -> poop(x, false));
        if (!stomach.canEat()) {
          goGetFood();
        }
        if (growth > .8f && !pen.isOverCrowded()) {
          reproduce();
        }
      }
    }
  }

    private void grow() {
        growth = Math.min(1f, growth + 0.01f);
        stomach.updateStomachSize(size, growth);
    }

    private void yuckEmote() {
    BuildingScreen.other.add(new YuckEmote(renderable.getWorldCoord(MOUTH_ANCHOR)));
  }

  private void poop(Food poop, boolean withOffset) {
      FoodDispenser target;
      if (pen.getDump().getAllocatedFoods()>=pen.getDump().getMaxFood()){
          target = pen.getDispenser();
      }else{
          target = pen.getDump();
      }
    BuildingScreen.other.add(
        new FoodPoop(renderable.getWorldCoord(MOUTH_ANCHOR), poop, target, withOffset, false, Optional.empty()));
  }

  private void reproduce() {
    if (RANDOM.nextFloat() < 0.05f) {
      Creature child = createCreature(getType(), pen, false);
      child.getRenderable().woordCoord = renderable.getWorldCoord().cpy();
      creatures.add(child);
    }
  }

  public static Creature createCreature(
      CreatureType type, Pen pen, final boolean alreadyGrown) {
    return switch (type) {
      case RAT -> new Rat(pen, alreadyGrown);
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
        && this.stomach.isHungry()) {
      final Vector2 dispenserPositionWorldCoord = pen.getDispenserPosition();
      this.state.walkTowardsWorldCoord(dispenserPositionWorldCoord, renderable.getWorldCoord());
    }
  }

  public void disposeCheck() {
    if (state.getState().equals(EntityState.State.DEAD) && (renderable.size.x < .1f)) {
      this.isDisposed = true;
    }
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

  private Vector2 getSizeVector() {
    return new Vector2(this.size, this.size).scl(this.growth);
  }

  abstract CreatureType getType();
}
