package io.github.creature.herder.player;

import static io.github.creature.herder.screen.BuildingScreen.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.creature.herder.creatures.Entity;
import io.github.creature.herder.creatures.EntityState;
import io.github.creature.herder.input.InputHelper;
import io.github.creature.herder.render.RenderableObject;
import io.github.creature.herder.util.CoordUtil;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Player extends Entity {
  public static final Texture PLAYER_TEXTURE = new Texture("player_spread.png");
  static float PLAYER_Y_OFFSET = -1f;
  public RenderableObject pickedUpObject;
  private int money = 1000000;
  private Direction direction;
  private Direction wannabeDirection;
  private long time;

  @Override
  protected Texture getTexture() {
    return PLAYER_TEXTURE;
  }

  @Override
  public void update(final float delta) {
    float deltaX = 0f;
    float deltaY = 0f;
    if (InputHelper.isWalkingRight()) {
      deltaX -= 1f;
      deltaY += 1f;
    }
    if (InputHelper.isWalkingLeft()) {
      deltaX += 1f;
      deltaY -= 1f;
    }
    if (InputHelper.isWalkingUp()) {
      deltaX += 1f;
      deltaY += 1f;
    }
    if (InputHelper.isWalkingDown()) {
      deltaX -= 1f;
      deltaY -= 1f;
    }
    if (deltaX != 0f || deltaY != 0f) {
      Direction newDirection = EntityState.determineDirectionWorldDelta(deltaX, deltaY);
      long newTime = Instant.now().toEpochMilli();
      if (wannabeDirection != newDirection) {
        wannabeDirection = newDirection;
        time = newTime;
      }
      if (newDirection != direction && (newTime - time) > 50) {
        direction = newDirection;
        state.setDirection(newDirection);
      }
      state.setState(EntityState.State.WALKING);
    } else {
      state.idle();
    }

    this.renderable.woordCoord =
        CoordUtil.ScreenToWorld(new Vector2(0f, 0f)).add(PLAYER_Y_OFFSET, PLAYER_Y_OFFSET);
  }

  @Override
  protected int getAnimationOffset(final EntityState entityState) {
    if (entityState.getState().equals(EntityState.State.WALKING)) {
      final int i = ((int) (TimeUtils.millis() / 200)) % 4;
      if (i == 0) {
        return 1;
      }
      if (i == 2) {
        return 3;
      }
      return 2;
    }
    return 2;
  }

  public static Vector2 computePickedUpWorldCoord() {
    final float offset = .5f;
    final Vector2 offsetVector =
        EntityState.directionVectors.get(player.getState().getDirection().ordinal()).cpy();
    return player.getRenderable().getWorldCoord().cpy().add(offsetVector.scl(offset, offset));
  }
}
