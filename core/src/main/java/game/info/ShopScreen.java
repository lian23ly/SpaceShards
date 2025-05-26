
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

public class ShopScreen implements Screen {

    private final MyGames game;
    private Stage stage;
    private AssetManager assetManager;
    private SpriteBatch batch;

    private Texture backBtnNormal;
    private Texture backBtnPressed;
    private Texture girlTexture;
    private Texture diamondTexture;
    private Texture islandTexture;
    private Texture animalTexture;
    private Texture backgroundTexture;

    public ShopScreen(final MyGames game) {
        batch = new SpriteBatch();
        this.game = game;
    }

    @Override
    public void show() {
        assetManager = new AssetManager();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        game.playDefaultMusic();
        backgroundTexture = new Texture(Gdx.files.internal("space1.png"));

        assetManager.load("back01.png", Texture.class);
        assetManager.load("back02.png", Texture.class);
        assetManager.load("girlforscreen.png", Texture.class);
        assetManager.load("stone.png", Texture.class);
        assetManager.load("chinamap.jpg", Texture.class);
        assetManager.load("icon_monster.png", Texture.class);
        assetManager.finishLoading();

        backBtnNormal = assetManager.get("back01.png", Texture.class);
        backBtnPressed = assetManager.get("back02.png", Texture.class);
        girlTexture = assetManager.get("girlforscreen.png", Texture.class);
        diamondTexture = assetManager.get("stone.png", Texture.class);
        islandTexture = assetManager.get("chinamap.jpg", Texture.class);
        animalTexture = assetManager.get("icon_monster.png", Texture.class);

        float buttonSize = Gdx.graphics.getWidth() * 0.2f;
        float animalButtonSize = buttonSize * 1f;
        float backButtonWidth = 550f;
        float backButtonHeight = 250f;

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        ImageButton.ImageButtonStyle backButtonStyle = new ImageButton.ImageButtonStyle();
        backButtonStyle.up = new TextureRegionDrawable(backBtnNormal);
        backButtonStyle.down = new TextureRegionDrawable(backBtnPressed);

        ImageButton girlButton = new ImageButton(new TextureRegionDrawable(girlTexture));
        ImageButton diamondButton = new ImageButton(new TextureRegionDrawable(diamondTexture));
        ImageButton islandButton = new ImageButton(new TextureRegionDrawable(islandTexture));
        ImageButton animalButton = new ImageButton(new TextureRegionDrawable(animalTexture));
        ImageButton backButton = new ImageButton(backButtonStyle);

        girlButton.setSize(buttonSize, buttonSize);
        diamondButton.setSize(buttonSize, buttonSize);
        islandButton.setSize(buttonSize, buttonSize);
        animalButton.setSize(animalButtonSize, animalButtonSize);
        backButton.setSize(backButtonWidth, backButtonHeight);

        Table topRowTable = new Table();
        topRowTable.defaults().pad(10).size(buttonSize);

        topRowTable.add(girlButton);
        topRowTable.add(diamondButton);
        topRowTable.add(islandButton);
        topRowTable.add(animalButton);

        Table bottomTable = new Table();
        bottomTable.add(backButton).size(backButtonWidth, backButtonHeight).padTop(50);

        mainTable.add(topRowTable).row();
        mainTable.add(bottomTable);

        girlButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new HeroesScreen(game));
            }
        });

        diamondButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new ImprovementsScreen(game));
            }
        });

        islandButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new MapsScreen(game));
            }
        });

        animalButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new PetsScreen(game));
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new GameScreen(game));
            }
        });
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
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (backBtnNormal != null) backBtnNormal.dispose();
        if (backBtnPressed != null) backBtnPressed.dispose();
        if (girlTexture != null) girlTexture.dispose();
        if (diamondTexture != null) diamondTexture.dispose();
        if (islandTexture != null) islandTexture.dispose();
        if (animalTexture != null) animalTexture.dispose();
    }
}
