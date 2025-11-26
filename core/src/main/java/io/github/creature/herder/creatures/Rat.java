package io.github.creature.herder.creatures;

import io.github.creature.herder.building.Pen;
import io.github.creature.herder.food.MeatEatingDigestionTrack;
import io.github.creature.herder.food.RotEatingDigestionTrack;
import java.util.List;

public class Rat extends Creature {

  public Rat(final Pen pen, final float size, final float digestionSpeed) {
    super(
        pen,
        size,
        digestionSpeed,
        List.of(new MeatEatingDigestionTrack(), new RotEatingDigestionTrack()));
  }

  @Override
  protected String getTextureFileName() {
    return "rat.png";
  }

  protected int getAnimationOffset(final EntityState entityState) {
    return 0;
  }

    @Override
    CreatureType getType() {
        return CreatureType.RAT;
    }
}
