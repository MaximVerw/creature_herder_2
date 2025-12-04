package io.github.creature.herder.screen;

import static io.github.creature.herder.entity.creatures.Creature.createCreature;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.creature.herder.building.Building;
import io.github.creature.herder.camera.WorldCamera;
import io.github.creature.herder.entity.creatures.Creature;
import io.github.creature.herder.entity.creatures.CreatureType;
import io.github.creature.herder.entity.creatures.Rat;
import io.github.creature.herder.entity.customer.Customer;
import io.github.creature.herder.entity.player.Player;
import io.github.creature.herder.input.InputHelper;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import io.github.creature.herder.screen.ui.UIHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class BuildingScreen implements Screen {
  public static Random RANDOM = new Random();
  public static Player player = new Player();
  public static List<Creature> creatures = new ArrayList<>();
  public static List<RenderableObject> other = new ArrayList<>();
  public static List<Customer> customers = new ArrayList<>();
  public static Building building = new Building();
  SpriteBatch spriteBatch;

  @Override
  public void show() {
    WorldCamera.resetCamera();
    spriteBatch = new SpriteBatch();

    for (int i = 0; i < 2; i++) {
      final Rat rat = new Rat(building.getPens().getFirst(), i * .4f);
      creatures.add(rat);
    }
    creatures.add(createCreature(CreatureType.STONER, building.getPens().get(1), .8f));
  }

  @Override
  public void render(final float delta) {
    InputHelper.processInputs(delta);
    WorldCamera.updateCamera();
    World.update(delta);

    ScreenUtils.clear(Color.BLACK);

    // creatures
    creatures.removeAll(creatures.stream().filter(Creature::isDisposed).toList());
    new ArrayList<>(creatures).forEach(creature -> creature.update(delta));

    // items
    other.removeAll(other.stream().filter(RenderableObject::isDisposed).toList());
    new ArrayList<>(other).forEach(item -> item.update(delta));
    other.removeAll(other.stream().filter(RenderableObject::isDisposed).toList());

    // customers
    customers.removeAll(customers.stream().filter(Customer::isDisposed).toList());
    customers.forEach(customer -> customer.update(delta));

    // player
    player.update(delta);

    drawSprites();
  }

  private void drawSprites() {
    final List<Renderable> renderables = new ArrayList<>(building.getRenderables());
    creatures.forEach(creature -> renderables.add(creature.getRenderable()));
    other.forEach(item -> renderables.add(item.getRenderable()));
    customers.forEach(customer -> renderables.add(customer.getRenderable()));
    renderables.add(player.getRenderable());
    renderables.addAll(UIHelper.drawUIRenderables());

    spriteBatch.setProjectionMatrix(WorldCamera.camera.combined);
    spriteBatch.begin();
    renderables.stream()
        .sorted(
            Comparator.comparingDouble(x -> ((Renderable) x).priority)
                .thenComparingDouble(x -> -((Renderable) x).getScreenCoord().y))
        .forEach(r -> drawRenderable(r, spriteBatch));
    UIHelper.drawUIElements(spriteBatch);
    spriteBatch.end();
  }

  public static void drawRenderable(Renderable renderable, SpriteBatch spriteBatch) {
    Vector2 bottomLeftCoord = renderable.getScreenCoord(new Vector2(0f, 0f));
    spriteBatch.draw(
        renderable.sprite,
        bottomLeftCoord.x,
        bottomLeftCoord.y,
        (renderable.flipX ? -1 : 1) * renderable.getScreenWidth(),
        renderable.getScreenHeight());
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
