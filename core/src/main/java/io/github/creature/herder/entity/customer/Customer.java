package io.github.creature.herder.entity.customer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.creature.herder.entity.Entity;
import io.github.creature.herder.entity.EntityState;
import io.github.creature.herder.entity.Direction;
import io.github.creature.herder.entity.creatures.Creature;
import io.github.creature.herder.entity.player.Player;
import io.github.creature.herder.screen.BuildingScreen;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
public class Customer extends Entity {
    public static final Texture CUSTOMER_TEXTURE = new Texture("player_spread.png");
    private Direction direction;
    List<Vector2> path;
    int slot;
    boolean purchased;

    public Customer(){
        super();
        speed = .5f;
        slot = findUnusedSlot();
        path = new ArrayList<>(generatePath());
        this.renderable.setWoordCoord(path.getFirst());
        purchased = false;
    }

    private List<Vector2> generatePath() {
        return List.of(new Vector2(-.5f, -1f), new Vector2(-.5f, slot), new Vector2(0f, slot));
    }

    public boolean buy(Creature creature){
        if (path.isEmpty() && creature.growth>.8f){
            path.addAll(generatePath().reversed());
            purchased = true;
            creature.isDisposed = true;
            BuildingScreen.player.setMoney((int) (BuildingScreen.player.getMoney() + creature.size*500f*((float) creature.health /creature.maxHealth)));
            return true;
        }else{
            return false;
        }
    }

    public static int findUnusedSlot() {
        Set<Integer> occupiedSlots = BuildingScreen.customers.stream().map(Customer::getSlot).collect(Collectors.toSet());
        int i = 1;
        while (true){
            if (!occupiedSlots.contains(i)){
                return i;
            }
            i += 1;
        }
    }

    @Override
    protected Texture getTexture() {
        return CUSTOMER_TEXTURE;
    }

    @Override
    public void update(final float delta) {
        if (path.isEmpty()){
            state.idle();
            if (purchased){
                isDisposed = true;
            }
        }else{
            Vector2 translation = path.getFirst().cpy().sub(renderable.woordCoord);
            if (translation.len() <= delta*speed){
                path.removeFirst();
            }else{
                translation.nor().scl(delta*speed);
            }
            state.setDirection(EntityState.determineDirectionWorldDelta(translation.x, translation.y));
            state.setState(EntityState.State.WALKING);
            this.renderable.woordCoord.add(translation);
        }
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
