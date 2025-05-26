package game.info;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
public class GameOverScreen implements Screen {
    private final MyGames game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    private Texture gameOverTexture;
    private Texture restartButtonTexture;
    private Texture restartButtonPressedTexture;
    private Texture homeButtonTexture;
    private Texture homeButtonPressedTexture;
    private Texture diamondTexture;
    private Texture spellTexture;
    private BitmapFont font;
    private Texture backgroundTexture;

    public GameOverScreen(final MyGames game) {
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
        gameOverTexture = new Texture(Gdx.files.internal("gameover.png"));
        restartButtonTexture = new Texture(Gdx.files.internal("restart01.png"));
        restartButtonPressedTexture = new Texture(Gdx.files.internal("restart02.png"));
        homeButtonTexture = new Texture(Gdx.files.internal("home01.png"));
        homeButtonPressedTexture = new Texture(Gdx.files.internal("home02.png"));
        diamondTexture = new Texture(Gdx.files.internal("stone.png"));
        spellTexture = new Texture(Gdx.files.internal("spell.png"));
        font = new BitmapFont();
        font.getData().setScale(3f);
        backgroundTexture = new Texture(Gdx.files.internal("space1.png"));

    }

    private void createUI() {
        float gameOverScale = 0.8f;
        float gameOverWidth = gameOverTexture.getWidth() * gameOverScale;
        float gameOverHeight = gameOverTexture.getHeight() * gameOverScale;

        float buttonScale = 0.8f;
        float buttonWidth = restartButtonTexture.getWidth() * buttonScale;
        float buttonHeight = restartButtonTexture.getHeight() * buttonScale;

        float centerX = Gdx.graphics.getWidth() / 2f;

        float buttonsY = Gdx.graphics.getHeight() * 0.4f;

        float gameOverY = buttonsY + buttonHeight + 50;

        // Restart Button
        ImageButton.ImageButtonStyle restartStyle = new ImageButton.ImageButtonStyle();
        restartStyle.up = new TextureRegionDrawable(restartButtonTexture);
        restartStyle.down = new TextureRegionDrawable(restartButtonPressedTexture);

        ImageButton restartButton = new ImageButton(restartStyle);
        restartButton.setSize(buttonWidth, buttonHeight);
        restartButton.setPosition(centerX - buttonWidth / 2, buttonsY);
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new ForestScreen(game));
                dispose();
            }
        });

        ImageButton.ImageButtonStyle homeStyle = new ImageButton.ImageButtonStyle();
        homeStyle.up = new TextureRegionDrawable(homeButtonTexture);
        homeStyle.down = new TextureRegionDrawable(homeButtonPressedTexture);

        ImageButton homeButton = new ImageButton(homeStyle);
        homeButton.setSize(buttonWidth, buttonHeight);
        homeButton.setPosition(centerX - buttonWidth / 2, buttonsY - buttonHeight - 30);
        homeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        stage.addActor(restartButton);
        stage.addActor(homeButton);
    }

    @Override
    public void show() { game.playDefaultMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float gameOverScale = 0.8f;
        float gameOverWidth = gameOverTexture.getWidth() * gameOverScale;
        float gameOverHeight = gameOverTexture.getHeight() * gameOverScale;
        float gameOverX = Gdx.graphics.getWidth() / 2f - gameOverWidth / 2;
        float gameOverY = Gdx.graphics.getHeight() * 0.4f + restartButtonTexture.getHeight() * 0.8f - 80;
        batch.draw(gameOverTexture, gameOverX, gameOverY, gameOverWidth, gameOverHeight);

        if (font != null && diamondTexture != null && spellTexture != null) {
            float RESOURCE_SIZE = 100f;
            float PADDING_RIGHT = 15f;
            float PADDING_TOP = 15f;
            float TEXT_ICON_SPACING = 5f;

            String spellsText = String.valueOf(game.spellsCollected);
            String diamondsText = String.valueOf(MyGames.diamonds);

            GlyphLayout spellsLayout = new GlyphLayout(font, spellsText);
            GlyphLayout diamondsLayout = new GlyphLayout(font, diamondsText);

            float currentX = Gdx.graphics.getWidth() - PADDING_RIGHT;
            float yPos = Gdx.graphics.getHeight() - PADDING_TOP - RESOURCE_SIZE;

            float diamondsIconX = currentX - RESOURCE_SIZE;
            float diamondsTextX = diamondsIconX - TEXT_ICON_SPACING - diamondsLayout.width;
            batch.draw(diamondTexture, diamondsIconX, yPos, RESOURCE_SIZE, RESOURCE_SIZE);
            font.draw(batch, diamondsText, diamondsTextX, yPos + RESOURCE_SIZE/2 + diamondsLayout.height/2);

            currentX = diamondsTextX - PADDING_RIGHT;
            float spellsIconX = currentX - RESOURCE_SIZE;
            float spellsTextX = spellsIconX - TEXT_ICON_SPACING - spellsLayout.width;
            batch.draw(spellTexture, spellsIconX, yPos, RESOURCE_SIZE, RESOURCE_SIZE);
            font.draw(batch, spellsText, spellsTextX, yPos + RESOURCE_SIZE/2 + spellsLayout.height/2);
        }


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

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {

        if (gameOverTexture != null) {
            gameOverTexture.dispose();
            gameOverTexture = null;
        }
        if (restartButtonTexture != null) {
            restartButtonTexture.dispose();
            restartButtonTexture = null;
        }
        if (restartButtonPressedTexture != null) {
            restartButtonPressedTexture.dispose();
            restartButtonPressedTexture = null;
        }
        if (homeButtonTexture != null) {
            homeButtonTexture.dispose();
            homeButtonTexture = null;
        }
        if (homeButtonPressedTexture != null) {
            homeButtonPressedTexture.dispose();
            homeButtonPressedTexture = null;
        }
        if (diamondTexture != null) {
            diamondTexture.dispose();
            diamondTexture = null;
        }
        if (spellTexture != null) {
            spellTexture.dispose();
            spellTexture = null;
        }
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (stage != null) {
            stage.dispose();
            stage = null;
        }

        batch = null;
        gameOverTexture = null;
        restartButtonTexture = null;
        restartButtonPressedTexture = null;
        homeButtonTexture = null;
        homeButtonPressedTexture = null;
        diamondTexture = null;
        spellTexture = null;
        font = null;
        stage = null;

        Gdx.app.log("GameOverScreen", "Disposed");
    }
}
