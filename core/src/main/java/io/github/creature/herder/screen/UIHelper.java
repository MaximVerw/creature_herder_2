package io.github.creature.herder.screen;

import static io.github.creature.herder.screen.BuildingScreen.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

public class UIHelper {
  private static final List<TextureRegion> numbers = getNumberTextures();
  private static final int UI_SCALE_FACTOR = 3;

  private UIHelper() {}

  private static List<TextureRegion> getNumberTextures() {
    final Texture numbersTexture = new Texture(Gdx.files.getLocalStoragePath() + "numbers.png");
    final List<TextureRegion> numberTextures = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      numberTextures.add(new TextureRegion(numbersTexture, 7 * i, 0, 7, 10));
    }
    return numberTextures;
  }

  public static void drawUIElements(final SpriteBatch spriteBatch) {
    drawMoney(spriteBatch);
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
