package game.info;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LevelsScreen implements Screen {

    private final MyGames game;
    private Stage stage;
    private AssetManager assetManager;
    private Texture backBtnNormal;
    private Texture backBtnPressed;
    private Texture forestBtnNormal;
    private Texture dungeonBtnNormal;
    private Texture cloudBtnNormal;
    private Texture backgroundTexture;
    private SpriteBatch batch;

    private static final float BACK_BUTTON_SCALE = 0.9f;
    private static final float LEVEL_BUTTON_SCALE = 0.7f;

    public LevelsScreen(final MyGames game) {
        this.game = game;
        this.assetManager = new AssetManager();
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        assetManager.load("back01.png", Texture.class);
        assetManager.load("back02.png", Texture.class);
        assetManager.load("forest1.jpg", Texture.class);
        assetManager.load("dungeon1.jpg", Texture.class);
        assetManager.load("cloudmap.jpg", Texture.class);
        assetManager.finishLoading();

        backBtnNormal = assetManager.get("back01.png", Texture.class);
        backBtnPressed = assetManager.get("back02.png", Texture.class);
        forestBtnNormal = assetManager.get("forest1.jpg", Texture.class);
        dungeonBtnNormal = assetManager.get("dungeon1.jpg", Texture.class);
        cloudBtnNormal = assetManager.get("cloudmap.jpg", Texture.class);
        backgroundTexture = new Texture(Gdx.files.internal("space1.png"));

        ImageButton backButton = createBackButton();
        ImageButton forestButton = createLevelButton(forestBtnNormal, new ForestScreen(game));
        ImageButton dungeonButton = createLevelButton(dungeonBtnNormal, new DungeonScreen(game));
        ImageButton cloudButton = createLevelButton(cloudBtnNormal, new CloudScreen(game));

        float dungeonWidth = dungeonBtnNormal.getWidth() * LEVEL_BUTTON_SCALE;
        float backWidth = backBtnNormal.getWidth() * BACK_BUTTON_SCALE;
        float offsetX = (dungeonWidth - backWidth) / 2;

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Table levelsTable = new Table();
        levelsTable.add(forestButton)
            .size(forestBtnNormal.getWidth() * LEVEL_BUTTON_SCALE,
                forestBtnNormal.getHeight() * LEVEL_BUTTON_SCALE)
            .padRight(50);

        levelsTable.add(dungeonButton)
            .size(dungeonWidth,
                dungeonBtnNormal.getHeight() * LEVEL_BUTTON_SCALE)
            .padRight(50);

        levelsTable.add(cloudButton)
            .size(cloudBtnNormal.getWidth() * LEVEL_BUTTON_SCALE,
                cloudBtnNormal.getHeight() * LEVEL_BUTTON_SCALE);

        Table backTable = new Table();
        backTable.add().width(offsetX);
        backTable.add(backButton)
            .size(backWidth, backBtnNormal.getHeight() * BACK_BUTTON_SCALE)
            .padTop(20);

        mainTable.add(levelsTable).row();
        mainTable.add(backTable).padTop(30);

        stage.addActor(mainTable);
    }

    private ImageButton createBackButton() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(backBtnNormal);
        style.down = new TextureRegionDrawable(backBtnPressed);

        ImageButton button = new ImageButton(style);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new GameScreen(game));
            }
        });
        return button;
    }

    private ImageButton createLevelButton(Texture texture, final Screen screen) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(texture);

        ImageButton button = new ImageButton(style);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(screen);
            }
        });
        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }



    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        assetManager.unload("back01.png");
        assetManager.unload("back02.png");
        assetManager.unload("forest1.jpg");
        assetManager.unload("dungeon1.jpg");
        assetManager.unload("cloudmap.jpg");
    }

    @Override
    public void dispose() {
        stage.dispose();
        assetManager.dispose();
        batch.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
