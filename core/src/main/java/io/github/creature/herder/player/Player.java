package io.github.creature.herder.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.creature.herder.camera.WorldCamera;
import io.github.creature.herder.creatures.Entity;
import io.github.creature.herder.creatures.EntityState;
import io.github.creature.herder.util.CoordUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Player extends Entity {
  static float PLAYER_OFFSET_Y = 2f;

  @Override
  protected String getTextureFileName() {
    return "player_spread.png";
  }

  @Override
  public void update(final float delta, final Player player) {
    Vector2 screenCoord =
        CoordUtil.WorldToScreen(WorldCamera.position.cpy().sub(0f, PLAYER_OFFSET_Y));
    player.renderable.sprite.setPosition(screenCoord.x, screenCoord.y);
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
