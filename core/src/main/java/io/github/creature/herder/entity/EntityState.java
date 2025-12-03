package io.github.creature.herder.entity;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityState {
  Vector2 targetWorldCoord;
  State state;
  Direction direction;

  // in worldCoord
  public static List<Vector2> directionVectors = setupDirectionVectors();

  EntityState() {
    targetWorldCoord = new Vector2(0, 0);
    state = State.IDLE;
    direction = Direction.North;
  }

  private static List<Vector2> setupDirectionVectors() {
    List<Vector2> result = new ArrayList<>();
    result.add(new Vector2(1, 1).nor());
    result.add(new Vector2(0, 1).nor());
    result.add(new Vector2(-1, 1).nor());
    result.add(new Vector2(-1, 0).nor());
    result.add(new Vector2(-1, -1).nor());
    result.add(new Vector2(0, -1).nor());
    result.add(new Vector2(1, -1).nor());
    result.add(new Vector2(1, 0).nor());
    return result;
  }

  public void walkTowardsWorldCoord(final Vector2 target, final Vector2 currentPosition) {
    targetWorldCoord = target;
    state = State.WALKING;
    Vector2 delta = target.cpy().sub(currentPosition);
    if (delta.x != 0 || delta.y != 0) {
      direction = determineDirectionWorldDelta(delta.x, delta.y);
    }
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

  public static Direction determineDirectionWorldDelta(final float deltaX, final float deltaY) {
    final Vector2 vector2 = new Vector2(deltaX, deltaY).nor();

    final List<Float> distances =
        directionVectors.stream().map(directionVector -> directionVector.dst2(vector2)).toList();

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
