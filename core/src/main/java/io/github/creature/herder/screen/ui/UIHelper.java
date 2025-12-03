package io.github.creature.herder.screen.ui;

import static io.github.creature.herder.screen.BuildingScreen.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.util.ColorTextureHelper;
import io.github.creature.herder.util.ColorType;
import io.github.creature.herder.util.CoordUtil;
import java.util.ArrayList;
import java.util.List;

public class UIHelper {
  private static final Texture NUMBERS_TEXTURE =
      new Texture(Gdx.files.getLocalStoragePath() + "numbers.png");
  private static final List<TextureRegion> numbers = getNumberTextures();
  private static final int UI_SCALE_FACTOR = 3;
  public static final float PANEL_SIZE = 1f;

  private UIHelper() {}

  private static List<TextureRegion> getNumberTextures() {
    final List<TextureRegion> numberTextures = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      numberTextures.add(new TextureRegion(NUMBERS_TEXTURE, 7 * i, 0, 7, 10));
    }
    return numberTextures;
  }

  public static void drawUIElements(final SpriteBatch spriteBatch) {
    drawMoney(spriteBatch);
  }

  public static List<Renderable> drawUIRenderables() {
    return getCreaturePanelRenderables();
  }

  private static List<Renderable> getCreaturePanelRenderables() {
    List<Renderable> renderables = new ArrayList<>();
    if (other.stream().anyMatch(UIStomach.class::isInstance)) {
      renderables.add(getPanelRenderable());
    }
    return renderables;
  }

  public static Renderable getPanelRenderable() {
    Texture texture = ColorTextureHelper.getTexture(.2f, ColorType.BROWN);
    return new Renderable(
        texture,
        CoordUtil.ScreenToWorld(getPanelPosition()),
        new Vector2(PANEL_SIZE, PANEL_SIZE),
        new Vector2(.5f, .5f),
        9);
  }

  public static Vector2 getPanelPosition() {
    int screenWidth = Gdx.graphics.getWidth();
    int screenHeight = Gdx.graphics.getHeight();
    float panelOffset = screenHeight * .2f;
    return new Vector2(screenWidth * .5f - panelOffset, screenHeight * .5f - panelOffset);
  }

  public static void drawMoney(final SpriteBatch spriteBatch) {
    final int width = Gdx.graphics.getWidth();
    final int height = Gdx.graphics.getHeight();
    int counter = 0;
    List<Integer> digits = new ArrayList<>();
    int magnitude = (int) Math.pow(10, counter);
    while (counter < 3 || magnitude < player.getMoney()) {
      digits.addFirst((player.getMoney() / magnitude) % 10);
      counter += 1;
      magnitude = (int) Math.pow(10, counter);
    }
    for (int i = 0; i < digits.size(); i++) {
      int digit = digits.get(i);
      final int uiScaleFactor = UI_SCALE_FACTOR;
      final int numberWidth = numbers.getFirst().getRegionWidth() * uiScaleFactor;
      final int numberHeight = numbers.getFirst().getRegionHeight() * uiScaleFactor;
      final float posX = -width * .5f + numberWidth;
      final float posY = height * .5f - numberHeight - numberWidth;
      spriteBatch.draw(numbers.get(digit), posX + numberWidth * i, posY, numberWidth, numberHeight);
      counter += 1;
    }
  }
}
