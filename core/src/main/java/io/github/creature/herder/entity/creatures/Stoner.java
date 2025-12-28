package io.github.creature.herder.entity.creatures;

import com.badlogic.gdx.graphics.Texture;
import io.github.creature.herder.building.Pen;
import io.github.creature.herder.entity.EntityState;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.food.MeatEatingDigestionTrack;
import java.util.List;

public class Stoner extends Creature {
  public static final Texture STONER_TEXTURE = new Texture("images/stoner.png");

  public Stoner(final Pen pen, final float growth) {
    super(pen, 1f, .8f, 10, List.of(new MeatEatingDigestionTrack()), growth);
  }

  @Override
  protected Texture getTexture() {
    return STONER_TEXTURE;
  }

  @Override
  CreatureType getType() {
    return CreatureType.STONER;
  }

    @Override
    protected float getStomachSizeFactor() {
        return 5f;
    }

    @Override
    protected boolean reproduceRequirement() {
      if (stomach.food.stream().filter(Food.STONE::equals).count()>=5){
          for (int i = 0; i < 5; i++) {
              stomach.food.remove(Food.STONE);
          }
          return true;
      }
      return false;
    }

    protected int getAnimationOffset(final EntityState entityState) {
        return 0;
    }
}
