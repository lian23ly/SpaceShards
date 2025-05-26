package game.info;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

public class MainMenuScreen implements Screen {

    private final MyGames game;
    private Stage stage;
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private Texture playBtnNormal;
    private Texture playBtnPressed;
    private Texture optionBtnNormal;
    private Texture optionBtnPressed;

    public MainMenuScreen(final MyGames game) {

        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        loadTextures();
        createUI();
    }

    private void loadTextures() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("space1.png"));
            playBtnNormal = new Texture(Gdx.files.internal("play01.png"));
            playBtnPressed = new Texture(Gdx.files.internal("play02.png"));
            optionBtnNormal = new Texture(Gdx.files.internal("option01.png"));
            optionBtnPressed = new Texture(Gdx.files.internal("option02.png"));
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "Ошибка при загрузке текстур", e);
        }
    }

    private void createUI() {
        ImageButton.ImageButtonStyle playButtonStyle = new ImageButton.ImageButtonStyle();
        playButtonStyle.up = new TextureRegionDrawable(playBtnNormal);
        playButtonStyle.down = new TextureRegionDrawable(playBtnPressed);
        playButtonStyle.over = new TextureRegionDrawable(playBtnPressed);

        ImageButton.ImageButtonStyle optionButtonStyle = new ImageButton.ImageButtonStyle();
        optionButtonStyle.up = new TextureRegionDrawable(optionBtnNormal);
        optionButtonStyle.down = new TextureRegionDrawable(optionBtnPressed);
        optionButtonStyle.over = new TextureRegionDrawable(optionBtnPressed);

        ImageButton playButton = new ImageButton(playButtonStyle);
        ImageButton optionButton = new ImageButton(optionButtonStyle);

        float buttonScale = 1f;
        if (playBtnNormal != null) {
            playButton.setSize(playBtnNormal.getWidth() * buttonScale, playBtnNormal.getHeight() * buttonScale);
        }
        if (optionBtnNormal != null) {
            optionButton.setSize(optionBtnNormal.getWidth() * buttonScale, optionBtnNormal.getHeight() * buttonScale);
        }

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new GameScreen(game));
            }
        });

        optionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new SettingsScreen(game));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(playButton)
            .width(playButton.getWidth())
            .height(playButton.getHeight())
            .padBottom(30f)
            .row();

        table.add(optionButton)
            .width(optionButton.getWidth())
            .height(optionButton.getHeight());

        table.center();
    }

    @Override
    public void show() {

        game.playDefaultMusic();

        Gdx.input.setInputProcessor(stage);
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
    public void resume() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (playBtnNormal != null) playBtnNormal.dispose();
        if (playBtnPressed != null) playBtnPressed.dispose();
        if (optionBtnNormal != null) optionBtnNormal.dispose();
        if (optionBtnPressed != null) optionBtnPressed.dispose();
    }
}
