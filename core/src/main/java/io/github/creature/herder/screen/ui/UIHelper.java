package io.github.creature.herder.screen.ui;

import static io.github.creature.herder.input.InputHelper.getClosestRenderableObject;
import static io.github.creature.herder.screen.BuildingScreen.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.creature.herder.building.Store;
import io.github.creature.herder.entity.creatures.Creature;
import io.github.creature.herder.entity.customer.Customer;
import io.github.creature.herder.render.ClosestObjectWithDistance;
import io.github.creature.herder.render.Renderable;
import io.github.creature.herder.render.RenderableObject;
import io.github.creature.herder.util.ColorTextureHelper;
import io.github.creature.herder.util.ColorType;
import io.github.creature.herder.util.CoordUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UIHelper {
    private static final Texture NUMBERS_TEXTURE =
      new Texture(Gdx.files.getLocalStoragePath() + "images/numbers.png");
  private static final Texture HEART_TEXTURE =
      new Texture(Gdx.files.getLocalStoragePath() + "images/heart.png");
  private static final Texture GROWTH_TEXTURE =
      new Texture(Gdx.files.getLocalStoragePath() + "images/growth.png");
  private static final List<TextureRegion> numbers = getNumberTextures();
  private static final int UI_SCALE_FACTOR = 3;
  public static final float PANEL_SIZE = 1f;
    public static final Texture PANEL_COLOR_TEXTURE = ColorTextureHelper.getTexture(.2f, ColorType.BROWN);

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
    getUiStomachOpt().ifPresent(uiStomach -> drawCreatureStats(uiStomach.creature, spriteBatch));
    drawPopUp(spriteBatch);
  }

  private static void drawPopUp(SpriteBatch spriteBatch){
      ArrayList<RenderableObject> popupable = new ArrayList<>(building.getStores());
      popupable.addAll(customers);
      Optional<ClosestObjectWithDistance> closestPopUp =
          getClosestRenderableObject(
              popupable,
              player.getRenderable().getWorldCoord(),
              false);

      closestPopUp.filter(p -> p.distance()<1f).map(ClosestObjectWithDistance::object).ifPresent(closest -> {
          if (closest instanceof Customer customer){
              String customerText = customer.getText(player.pickedUpObject);
              drawPopUpText(customerText, customer.getRenderable().getWorldCoord(new Vector2(.5f, 1f)), spriteBatch);
          } else if (closest instanceof Store store) {
              drawPopUpText("Price: "+store.getPrice(), store.getRenderable().getWorldCoord(new Vector2(.3f,.6f)), spriteBatch);
          }
      });
  }

    private static void drawPopUpText(String text, Vector2 worldCoord, SpriteBatch spriteBatch) {
        Table table = new Table();
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = new BitmapFont();
        style.fontColor = Color.BLACK;
        style.background = new TextureRegionDrawable(PANEL_COLOR_TEXTURE);
        Vector2 screenCoord = CoordUtil.WorldToScreen(worldCoord);
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(style.font, text);
        table.setPosition(screenCoord.x, screenCoord.y);
        TextField textField = new TextField(text, style);
        textField.setAlignment(1);

        //table.add(textField).width(glyphLayout.width).height(glyphLayout.height).expand();
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.BLACK);
        labelStyle.background = new TextureRegionDrawable(PANEL_COLOR_TEXTURE);
        Label label = new Label(text, labelStyle);
        label.setWrap(true);
        table.add(label).width(glyphLayout.width).expand();
        table.draw(spriteBatch, 1f);
    }

    private static float getSize(String text, TextField.TextFieldStyle style, boolean width) {
        String[] split = text.split("\\n");
        if(width){
            int num = Arrays.stream(split).mapToInt(String::length).max().orElse(0);
            return style.font.getXHeight()* num + style.font.getSpaceXadvance()*(num-1);
        }else{
            return style.font.getLineHeight()*split.length;
        }
    }


    public static List<Renderable> drawUIRenderables() {
    return getCreaturePanelRenderables();
  }

  private static List<Renderable> getCreaturePanelRenderables() {
    List<Renderable> renderables = new ArrayList<>();
    Optional<UIStomach> uiStomachOpt = getUiStomachOpt();

    uiStomachOpt.ifPresent(uiStomach -> renderables.add(getPanelRenderable()));
    return renderables;
  }

  private static Optional<UIStomach> getUiStomachOpt() {
    return other.stream().filter(UIStomach.class::isInstance).map(UIStomach.class::cast).findAny();
  }

  public static Renderable getPanelRenderable() {
    return new Renderable(
        PANEL_COLOR_TEXTURE,
        CoordUtil.ScreenToWorld(
            new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f).sub(50, 50)),
        new Vector2(PANEL_SIZE, PANEL_SIZE * 1.2f),
        new Vector2(1f, 1f),
        9);
  }

  private static void drawCreatureStats(Creature creature, SpriteBatch spriteBatch) {
    int growth = (int) Math.floor(creature.growth * 100f);
    int health = creature.health;
    Renderable panelRenderable = getPanelRenderable();

    Vector2 heartPosition = panelRenderable.getScreenCoord(new Vector2(.3f, .8f));
    Vector2 growthPosition = panelRenderable.getScreenCoord(new Vector2(.3f, .6f));
    Vector2 heartNumberPos = panelRenderable.getScreenCoord(new Vector2(.5f, .8f));
    Vector2 growthNumberPos = panelRenderable.getScreenCoord(new Vector2(.5f, .6f));
    int numberHeight = getNumberHeight();
    spriteBatch.draw(HEART_TEXTURE, heartPosition.x, heartPosition.y, numberHeight, numberHeight);
    visualiseNumber(spriteBatch, health, heartNumberPos);
    spriteBatch.draw(
        GROWTH_TEXTURE, growthPosition.x, growthPosition.y, numberHeight, numberHeight);
    visualiseNumber(spriteBatch, growth, growthNumberPos);
  }

  public static void drawMoney(final SpriteBatch spriteBatch) {
    int number = player.getMoney();

    final int width = Gdx.graphics.getWidth();
    final int height = Gdx.graphics.getHeight();
    final float posX = -width * .5f + getNumberWidth();
    final float posY = height * .5f - getNumberHeight() - getNumberWidth();
    Vector2 position = new Vector2(posX, posY);
    visualiseNumber(spriteBatch, number, position);
  }

  private static void visualiseNumber(SpriteBatch spriteBatch, int number, Vector2 position) {
    List<Integer> digits = new ArrayList<>();
    int counter = 0;
    int magnitude = (int) Math.pow(10, counter);
    while (counter < 2 || magnitude <= number) {
      digits.addFirst((number / magnitude) % 10);
      counter += 1;
      magnitude = (int) Math.pow(10, counter);
    }
    for (int i = 0; i < digits.size(); i++) {
      int digit = digits.get(i);
      final int numberWidth = getNumberWidth();
      final int numberHeight = getNumberHeight();
      spriteBatch.draw(
          numbers.get(digit), position.x + numberWidth * i, position.y, numberWidth, numberHeight);
      counter += 1;
    }
  }

  private static int getNumberHeight() {
    return numbers.getFirst().getRegionHeight() * UI_SCALE_FACTOR;
  }

  private static int getNumberWidth() {
    return numbers.getFirst().getRegionWidth() * UI_SCALE_FACTOR;
  }
}
