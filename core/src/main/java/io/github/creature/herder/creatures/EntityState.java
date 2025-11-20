package io.github.creature.herder.creatures;

import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.player.Direction;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityState {
  Vector2 targetScreenCoord;
  State state;
  Direction direction;

  static List<Vector2> directionVectors = setupDirectionVectors();

  EntityState() {
    targetScreenCoord = new Vector2(0, 0);
    state = State.IDLE;
    direction = Direction.North;
  }

  private static List<Vector2> setupDirectionVectors() {
    List<Vector2> result = new ArrayList<>();
    result.add(new Vector2(0, 1).nor());
    result.add(new Vector2(1, 1).nor());
    result.add(new Vector2(1, 0).nor());
    result.add(new Vector2(1, -1).nor());
    result.add(new Vector2(0, -1).nor());
    result.add(new Vector2(-1, -1).nor());
    result.add(new Vector2(-1, 0).nor());
    result.add(new Vector2(-1, 1).nor());
    return result;
  }

  public void walkTowardsScreenCoord(final Vector2 target, final Vector2 currentPosition) {
    targetScreenCoord = target;
    state = State.WALKING;
    direction =
        determineDirectionScreenDelta(target.x - currentPosition.x, target.y - currentPosition.y);
  }

  public void idle() {
    state = State.IDLE;
  }

  public void die() {
    state = State.DEAD;
  }

  public void pickUp() {
    state = State.PICKED_UP;
  }

  public static Direction determineDirectionScreenDelta(final float deltaX, final float deltaY) {
    final Vector2 vector2 = new Vector2(deltaX, deltaY * 2f).nor();

    final List<Float> distances =
        directionVectors.stream()
            .map(directionVector -> directionVector.dst2(vector2))
            .collect(Collectors.toList());

    int minDistanceIndex = 0;
    float minDistance = distances.getFirst();

    for (int i = 1; i < distances.size(); i++) {
      if (minDistance > distances.get(i)) {
        minDistance = distances.get(i);
        minDistanceIndex = i;
      }
    }
    return Direction.values()[minDistanceIndex];
  }

  public enum State {
    IDLE,
    WALKING,
    PICKED_UP,
    DEAD
  }
}
