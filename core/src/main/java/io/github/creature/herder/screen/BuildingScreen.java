package io.github.creature.herder.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.creature.herder.building.Building;
import io.github.creature.herder.camera.WorldCamera;
import io.github.creature.herder.creatures.Creature;
import io.github.creature.herder.creatures.Rat;
import io.github.creature.herder.input.InputHelper;
import io.github.creature.herder.player.Player;
import io.github.creature.herder.render.Renderable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class BuildingScreen implements Screen {
  public static Random RANDOM = new Random();
  public static Player player = new Player();
  public static List<Creature> creatures = new ArrayList<>();
  public static Building building = new Building();
  SpriteBatch spriteBatch;

  @Override
  public void show() {
    WorldCamera.resetCamera();
    spriteBatch = new SpriteBatch();

    building
        .getPens()
        .forEach(
            pen -> {
              for (int i = 0; i < 5; i++) {
                final Rat rat = new Rat(pen, .5f + RANDOM.nextFloat() * .6f - .3f, 1f);
                creatures.add(rat);
              }
            });
  }

  @Override
  public void render(final float delta) {
    InputHelper.processInputs(delta);
    WorldCamera.updateCamera();

    ScreenUtils.clear(Color.BLACK);

    // creatures
    creatures.removeAll(creatures.stream().filter(Creature::removeMe).toList());
    creatures.forEach(creature -> creature.update(delta, player));

    // player
    player.update(delta, player);

    drawSprites();
  }

  private void drawSprites() {
    final List<Renderable> renderables = new ArrayList<>(building.getRenderables());
    creatures.forEach(creature -> renderables.add(creature.getRenderable()));
    renderables.add(player.getRenderable());

    spriteBatch.setProjectionMatrix(WorldCamera.camera.combined);
    spriteBatch.begin();
    renderables.stream()
        .sorted(Comparator.comparingDouble(x -> ((Renderable)x).priority).thenComparingDouble(x -> -((Renderable)x).getScreenCoord().y))
        .forEach(r -> drawRenderable(r, spriteBatch));
    spriteBatch.end();
  }

  private void drawRenderable(Renderable renderable, SpriteBatch spriteBatch) {
    float width = renderable.getScreenWidth();
    float height = renderable.getScreenHeight();
    Vector2 position = renderable.getScreenCoord();
    spriteBatch.draw(
        renderable.sprite,
        (renderable.flipX ? width : 0) + position.x - width * renderable.anchor.x,
        position.y - height * renderable.anchor.y,
        (renderable.flipX ? -1 : 1) * width,
        height);
  }

  @Override
  public void resize(final int width, final int height) {
    WorldCamera.resizeCamera();
  }

  @Override
  public void pause() {
    // Invoked when your application is paused.
  }

  @Override
  public void resume() {
    // Invoked when your application is resumed after pause.
  }

  @Override
  public void hide() {
    // This method is called when another screen replaces this one.
  }

  @Override
  public void dispose() {
    // Destroy screen's assets here.
  }
}
