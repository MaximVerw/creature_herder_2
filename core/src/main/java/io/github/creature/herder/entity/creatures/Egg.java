package io.github.creature.herder.entity.creatures;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.creature.herder.building.Pen;
import io.github.creature.herder.entity.EntityState;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.food.MeatEatingDigestionTrack;

import java.util.List;

import static io.github.creature.herder.screen.BuildingScreen.creatures;

public class Egg extends Creature{
    public static final Texture EGG_TEXTURE = new Texture("images/egg.png");
    private static final float HATCH_TIME = 30f;
    private final CreatureType creatureType;

    Egg(Pen pen, CreatureType creatureType) {
        super(pen, 1f, 0, 1, List.of(), 0);
        this.creatureType = creatureType;
        this.state.idle();
    }

    @Override
    public void update(final float delta){
        switch (state.getState()){
            case IDLE -> {
                growth += delta/HATCH_TIME;
                if (growth>=1f){
                    Creature creature = createCreature(creatureType, pen, 0.2f);
                    creature.getRenderable().setWoordCoord(renderable.getWorldCoord().cpy());
                    creatures.add(creature);
                    this.dispose();
                }
            }case PICKED_UP -> pickedUpUpdate();
            default -> {}
        }
    }

    @Override
    protected TextureRegion getRegion(final EntityState state) {
        return new TextureRegion(
            EGG_TEXTURE,0,0,
            EGG_TEXTURE.getWidth(),
            EGG_TEXTURE.getHeight());
    }

    @Override
    public double getPrice(){
        return REPRODUCE_STEPS* Food.MEAT.getPrice()*2f*(1.+PRICE_PREMIUM);
    }

    @Override
    CreatureType getType() {
        return creatureType;
    }

    @Override
    protected Texture getTexture() {
        return EGG_TEXTURE;
    }
}
