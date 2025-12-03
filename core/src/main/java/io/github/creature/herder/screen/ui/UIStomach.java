package io.github.creature.herder.screen.ui;

import static io.github.creature.herder.screen.BuildingScreen.other;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.entity.creatures.Creature;
import io.github.creature.herder.entity.creatures.Stomach;
import io.github.creature.herder.food.Food;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import io.github.creature.herder.util.CoordUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UIStomach extends RenderableObject {
  private static final Texture STOMACH_TEXTURE =
      new Texture(Gdx.files.getLocalStoragePath() + "stomach.png");
  public static final int WIDTH_PIXELS = 30;
  public static final int HEIGHT_PIXELS = 24;
  public static final List<Integer> STOMACH_SIZE_BY_PIXEL_PER_FOOD = createStomachSizes();
  public static final float STOMACH_SIZE_STEP = .2f;

  private final Creature creature;
  private final List<RenderableObject> uiFoods;

  public UIStomach(Creature creature) {
    this.creature = creature;
    this.uiFoods = new ArrayList<>();
    renderable =
        new Renderable(
            STOMACH_TEXTURE, getWorldCoord(), getSize(creature.stomach), new Vector2(.5f, 0f), 10);
  }

  @Override
  public void update(float delta) {
    if (creature.isDisposed) {
      dispose();
    } else {
      renderable.setSize(getSize(creature.stomach));
      renderable.setWoordCoord(getWorldCoord());
      updateUiFoods();
    }
  }

  private static Vector2 getWorldCoord() {
    return UIHelper.getPanelRenderable().getWorldCoord(new Vector2(.5f, 0f));
  }

  public void updateUiFoods() {
    disposeExistingUiFood();
    List<Food> food = creature.stomach.getFood();
    float sizePerPixel = renderable.getScreenWidth() / 32f;

    float pixelsPerFood = getPixelsPerFood();
    List<Vector2> positions = getPositions(sizePerPixel, pixelsPerFood);

    Vector2 centerScreenCoord = renderable.getScreenCoord(new Vector2(.5f, .5f));
    for (int i = 0; i < food.size(); i++) {
      Vector2 relativePosition =
          CoordUtil.ScreenToWorld(centerScreenCoord.cpy().add(positions.get(i)));
      Texture foodTexture = food.get(i).getTexture();

      float desiredScreenWidth = sizePerPixel * pixelsPerFood;
      ;
      Renderable renderableFood =
          new Renderable(
              foodTexture, relativePosition, new Vector2(1f, 1f), new Vector2(.5f, .5f), 11);
      float rescaleSize = desiredScreenWidth / renderableFood.getScreenWidth();
      renderableFood.setSize(new Vector2(rescaleSize, rescaleSize));
      RenderableObject renderableObject = new RenderableObject(renderableFood, false);
      uiFoods.add(renderableObject);
      other.add(renderableObject);
    }
  }

  private float getPixelsPerFood() {
    for (int i = STOMACH_SIZE_BY_PIXEL_PER_FOOD.size() - 1; i > 0; i--) {
      if (STOMACH_SIZE_BY_PIXEL_PER_FOOD.get(i) > creature.stomach.getStomachSize()) {
        return i * STOMACH_SIZE_STEP;
      }
    }
    throw new RuntimeException();
  }

  private static List<Integer> createStomachSizes() {
    List<Integer> stomachSizes = new ArrayList<>(List.of(0));
    for (float i = 1f; i < 10f; i += STOMACH_SIZE_STEP) {
      stomachSizes.add(getPositions(1f, i).size());
    }
    return stomachSizes;
  }

  private static List<Vector2> getPositions(float sizePerPixel, float foodWidthInStomachPixels) {
    List<Vector2> positions = new ArrayList<>();
    float width = sizePerPixel * foodWidthInStomachPixels;
    // do not go all the way to the stomach end to avoid the straight edges
    for (float x = 0; x < sizePerPixel * WIDTH_PIXELS / 2f; x += width) {
      float minusY = onLowerEllipseCurve(x, sizePerPixel);
      float offsetY = (onLowerEllipseCurve(x + width / 2f, sizePerPixel) - minusY) / 2f;
      for (float y = .5f * width + offsetY; y < -2f * minusY - offsetY; y += width) {
        positions.add(new Vector2(x, minusY + y));
        if (x != 0) {
          positions.add(new Vector2(-x, minusY + y));
        }
      }
    }
    return positions.stream()
        .sorted(Comparator.comparingDouble(v -> v.y * 100f + Math.abs(v.x)))
        .toList();
  }

  private static float onLowerEllipseCurve(float x, float sizePerPixel) {
    float stomachWidth = WIDTH_PIXELS * sizePerPixel;
    float stomachHeight = HEIGHT_PIXELS * sizePerPixel;

    return (float) (-Math.sqrt(1 - 4 * x * x / (stomachWidth * stomachWidth)) * stomachHeight / 2f);
  }

  @Override
  public void dispose() {
    super.dispose();
    disposeExistingUiFood();
  }

  private void disposeExistingUiFood() {
    uiFoods.forEach(f -> f.setDisposed(true));
  }

  private static Vector2 getSize(Stomach stomach) {
    float size =
        (float)
            Math.clamp(
                0.83 * Math.log10(Math.max(1, stomach.getStomachSize() - 4) / 2f) + 0.178, .4f, 2f);
    return new Vector2(size, size);
  }
}
