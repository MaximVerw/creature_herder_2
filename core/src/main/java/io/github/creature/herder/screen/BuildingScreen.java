package io.github.creature.herder.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
  Building building;
  Player player;
  List<Creature> creatures;
  SpriteBatch spriteBatch;

  @Override
  public void show() {
    WorldCamera.resetCamera();
    spriteBatch = new SpriteBatch();

    building = new Building();
    player = new Player();
    creatures = new ArrayList<>();
    building
        .getPens()
        .forEach(
            pen -> {
              for (int i = 0; i < 5; i++) {
                final Rat rat = new Rat(pen, 1f + RANDOM.nextFloat() * 1.5f - .3f, 1f);
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
    player.getState().idle();

    drawSprites();
  }

  private void drawSprites() {
    // building
    final List<Renderable> renderables = new ArrayList<>(building.getRenderables());
    creatures.forEach(creature -> renderables.add(creature.getRenderable()));
    renderables.add(player.getRenderable());

    spriteBatch.setProjectionMatrix(WorldCamera.camera.combined);
    spriteBatch.begin();
    renderables.stream().sorted(Comparator.comparingDouble(x ->x.priority)).forEach(r -> drawRenderable(r, spriteBatch));
    spriteBatch.end();
  }

  private void drawRenderable(Renderable renderable, SpriteBatch spriteBatch) {
    spriteBatch.draw(
        renderable.sprite.getTexture(),
        (renderable.sprite.isFlipX()?renderable.sprite.getWidth():0) + renderable.sprite.getX() - renderable.sprite.getWidth() * renderable.anchor.x,
        renderable.sprite.getY() - renderable.sprite.getHeight() * renderable.anchor.y,
        (renderable.sprite.isFlipX()?-1:1)*renderable.sprite.getWidth(),
        renderable.sprite.getHeight());
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
