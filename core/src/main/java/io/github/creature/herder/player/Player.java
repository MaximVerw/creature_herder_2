package io.github.creature.herder.player;

import static io.github.creature.herder.camera.WorldCamera.ZOOM;

import com.badlogic.gdx.utils.TimeUtils;
import io.github.creature.herder.camera.WorldCamera;
import io.github.creature.herder.creatures.Creature;
import io.github.creature.herder.creatures.Entity;
import io.github.creature.herder.creatures.EntityState;
import io.github.creature.herder.input.InputHelper;
import io.github.creature.herder.util.CoordUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Player extends Entity {
  static float PLAYER_OFFSET_Y = -1f;
  public Creature pickedUpCreature;

  @Override
  protected String getTextureFileName() {
    return "player_spread.png";
  }

  @Override
  public void update(final float delta, final Player player) {
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
      state.setDirection(EntityState.determineDirectionWorldDelta(deltaX, deltaY));
      state.setState(EntityState.State.WALKING);
    } else {
      state.idle();
    }

    this.renderable.woordCoord =
        CoordUtil.ScreenToWorld(WorldCamera.position.cpy().scl(ZOOM))
            .add(PLAYER_OFFSET_Y, PLAYER_OFFSET_Y);
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
}
