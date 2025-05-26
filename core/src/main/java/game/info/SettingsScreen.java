package game.info;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SettingsScreen implements Screen {
    private final MyGames game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    // Textures for buttons
    private Texture restartBtnNormal;
    private Texture restartBtnPressed;
    private Texture musicBtnNormal;
    private Texture musicBtnPressed;
    private Texture backBtnNormal;
    private Texture backBtnPressed;
    private Texture backgroundTexture;


    private ImageButton restartButton;
    private ImageButton musicButton;
    private ImageButton backButton;

    public SettingsScreen(final MyGames game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        batch = new SpriteBatch();
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        loadAssets();
        createUI();
    }



    private void loadAssets() {
        restartBtnNormal = new Texture(Gdx.files.internal("restart01.png"));
        restartBtnPressed = new Texture(Gdx.files.internal("restart02.png"));
        musicBtnNormal = new Texture(Gdx.files.internal("music01.png"));
        musicBtnPressed = new Texture(Gdx.files.internal("music02.png"));
        backBtnNormal = new Texture(Gdx.files.internal("back01.png"));
        backBtnPressed = new Texture(Gdx.files.internal("back02.png"));
        backgroundTexture = new Texture(Gdx.files.internal("space1.png"));
    }

    private void createUI() {
        // Стили кнопок (остается без изменений)
        ImageButton.ImageButtonStyle restartStyle = new ImageButton.ImageButtonStyle();
        restartStyle.up = new TextureRegionDrawable(restartBtnNormal);
        restartStyle.down = new TextureRegionDrawable(restartBtnPressed);

        ImageButton.ImageButtonStyle musicStyle = new ImageButton.ImageButtonStyle();
        musicStyle.up = new TextureRegionDrawable(musicBtnNormal);
        musicStyle.down = new TextureRegionDrawable(musicBtnPressed);

        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();
        backStyle.up = new TextureRegionDrawable(backBtnNormal);
        backStyle.down = new TextureRegionDrawable(backBtnPressed);

        // Создаем кнопки
        restartButton = new ImageButton(restartStyle);
        musicButton = new ImageButton(musicStyle);
        backButton = new ImageButton(backStyle);

        // Рассчитываем позиции
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Желаемое расстояние от краев (можно регулировать)
        float margin = screenHeight * 0.15f; // 15% от высоты экрана

        // Позиция верхнего ряда кнопок
        float topRowY = screenHeight - margin - restartButton.getHeight();

        // Позиция нижней кнопки
        float bottomRowY = margin;

        // Центрирование по горизонтали
        float centerX = screenWidth / 2f;

        // Расположение кнопок
        restartButton.setPosition(centerX - restartButton.getWidth() - 20, topRowY);
        musicButton.setPosition(centerX + 20, topRowY);
        backButton.setPosition(centerX - backButton.getWidth() / 2, bottomRowY);

        // Добавление обработчиков событий (остается без изменений)
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new RestartScreen(game));
            }
        });

        updateMusicButtonStyle();
        musicButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.toggleMusic();
                updateMusicButtonStyle();
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        stage.addActor(restartButton);
        stage.addActor(musicButton);
        stage.addActor(backButton);
    }
    private void updateMusicButtonStyle() {
        ImageButton.ImageButtonStyle musicStyle = new ImageButton.ImageButtonStyle();
        if (game.isMusicEnabled()) {
            musicStyle.up = new TextureRegionDrawable(musicBtnNormal);
        } else {
            musicStyle.up = new TextureRegionDrawable(musicBtnPressed);

        }
        musicButton.setStyle(musicStyle);
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
        restartBtnNormal.dispose();
        restartBtnPressed.dispose();
        musicBtnNormal.dispose();
        musicBtnPressed.dispose();
        backBtnNormal.dispose();
        backBtnPressed.dispose();
        backgroundTexture.dispose();
    }

    @Override public void show() {   game.playDefaultMusic(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
