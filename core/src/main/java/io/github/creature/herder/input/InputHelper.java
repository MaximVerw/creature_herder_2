package io.github.creature.herder.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.camera.WorldCamera;

import static java.lang.System.exit;

public class InputHelper {

  private static final float WALKING_SPEED = .1f;

  public static void processInputs(final float delta) {
    if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
        Gdx.app.exit();
        exit(0);
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
      if (Gdx.graphics.isFullscreen()) {
        // If currently in fullscreen, switch to windowed mode
        Gdx.graphics.setWindowedMode(600, 400);
      } else {
        // If currently in windowed mode, switch to fullscreen
        // Get the current display mode (usually the primary monitor's resolution)
        final Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
        Gdx.graphics.setFullscreenMode(displayMode);
      }
    }

    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
      WorldCamera.position.add(new Vector2(WALKING_SPEED * delta, 0f));
    }
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
      WorldCamera.position.add(new Vector2(-WALKING_SPEED * delta, 0f));
    }
    if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
      WorldCamera.position.add(new Vector2(0f, WALKING_SPEED * delta / 2f));
    }
    if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
      WorldCamera.position.add(new Vector2(0f, -WALKING_SPEED * delta / 2f));
    }

    /**
     * if (Gdx.input.isKeyPressed(Input.Keys.O)) { if (highlightedCreature == null ||
     * !highlightedCreature.getState().getState().equals(EntityState.State.PICKED_UP)) {
     * resetHighlightedCreature(); final ClosestCreatureWithDistance closestCreature =
     * getClosestCreature( new Vector3(player.getSprite().getX() + player.getSprite().getWidth() /
     * 2f, player.getSprite().getY(), 0)); if
     * (Creature.IDLE_STATES.contains(closestCreature.getClosestCreature().getState().getState()) &&
     * closestCreature.getMinimumDistance() < 1f) { closestCreature.getClosestCreature().pickUp();
     * highlightedCreature = closestCreature.getClosestCreature(); } } } if
     * (Gdx.input.isKeyPressed(Input.Keys.P)&&highlightedCreature!= null){
     * if(highlightedCreature.getState().getState().equals(EntityState.State.PICKED_UP)) { final
     * List<Integer> isoTile =
     * IsometricUtil.screenIsoToIsoTile(highlightedCreature.getSprite().getX() +
     * highlightedCreature.getSprite().getWidth() / 2f, highlightedCreature.getSprite().getY());
     * final Optional<Pen> pen = building.isPen(isoTile.getFirst(), isoTile.getLast());
     * pen.ifPresent(value -> highlightedCreature.putInPen(value)); } }*
     */
  }
}
