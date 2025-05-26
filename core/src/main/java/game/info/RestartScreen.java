package game.info;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RestartScreen implements Screen {
    private final MyGames game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private Texture backgroundTexture;
    private BitmapFont font;
    private Label resetLabel;

    // Кнопки
    private ImageButton restartButton;
    private ImageButton backButton;
    private Texture restartBtnNormal;
    private Texture restartBtnPressed;
    private Texture backBtnNormal;
    private Texture backBtnPressed;

    public RestartScreen(final MyGames game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        batch = new SpriteBatch();
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("space1.png"));
        font = new BitmapFont();
        font.getData().setScale(6f);
        font.setColor(Color.WHITE);

        loadButtons();
        createUI();
    }

    private void loadButtons() {
        restartBtnNormal = new Texture(Gdx.files.internal("restart01.png"));
        restartBtnPressed = new Texture(Gdx.files.internal("restart02.png"));
        backBtnNormal = new Texture(Gdx.files.internal("back01.png"));
        backBtnPressed = new Texture(Gdx.files.internal("back02.png"));
    }

    private void createUI() {
        ImageButton.ImageButtonStyle restartStyle = new ImageButton.ImageButtonStyle();
        restartStyle.up = new TextureRegionDrawable(restartBtnNormal);
        restartStyle.down = new TextureRegionDrawable(restartBtnPressed);

        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();
        backStyle.up = new TextureRegionDrawable(backBtnNormal);
        backStyle.down = new TextureRegionDrawable(backBtnPressed);

        float scale = 0.8f;

        restartButton = new ImageButton(restartStyle);
        restartButton.setSize(
            restartBtnNormal.getWidth() * scale,
            restartBtnNormal.getHeight() * scale
        );

        backButton = new ImageButton(backStyle);
        backButton.setSize(
            restartButton.getWidth(),
            restartButton.getHeight()
        );

        restartButton.setPosition(
            Gdx.graphics.getWidth()/2f - restartButton.getWidth()/2,
            Gdx.graphics.getHeight()/2f - restartButton.getHeight()/2
        );

        backButton.setPosition(
            Gdx.graphics.getWidth()/2f - backButton.getWidth()/2,
            restartButton.getY() - backButton.getHeight() - 30
        );

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        resetLabel = new Label("RESET PROGRESS", labelStyle);
        resetLabel.setAlignment(Align.center);
        resetLabel.setSize(restartButton.getWidth(), 80);
        resetLabel.setPosition(
            Gdx.graphics.getWidth()/2f - restartButton.getWidth()/2,
            restartButton.getY() + restartButton.getHeight() + 100
        );

        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.resetPurchases();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new SettingsScreen(game));
            }
        });

        stage.addActor(restartButton);
        stage.addActor(backButton);
        stage.addActor(resetLabel);
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
        viewport.update(width, height, true);
        camera.position.set(width / 2f, height / 2f, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        backgroundTexture.dispose();
        font.dispose();
        restartBtnNormal.dispose();
        restartBtnPressed.dispose();
        backBtnNormal.dispose();
        backBtnPressed.dispose();
    }

    @Override public void show() { game.playDefaultMusic();}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
