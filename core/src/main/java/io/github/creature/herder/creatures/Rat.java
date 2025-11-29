package io.github.creature.herder.creatures;

import com.badlogic.gdx.graphics.Texture;
import io.github.creature.herder.building.Pen;
import io.github.creature.herder.food.MeatEatingDigestionTrack;
import io.github.creature.herder.food.RotEatingDigestionTrack;
import java.util.List;

public class Rat extends Creature {
  public static final Texture RAT_TEXTURE = new Texture("rat.png");

  public Rat(
      final Pen pen, final float size, final float digestionSpeed, final boolean alreadyGrown) {
    super(
        pen,
        size,
        digestionSpeed,
        List.of(new MeatEatingDigestionTrack(), new RotEatingDigestionTrack()),
        alreadyGrown);
    this.health = (int) (this.health * 1.5f);
  }

  @Override
  protected Texture getTexture() {
    return RAT_TEXTURE;
  }

  protected int getAnimationOffset(final EntityState entityState) {
    return 0;
  }

  @Override
  CreatureType getType() {
    return CreatureType.RAT;
  }
}
