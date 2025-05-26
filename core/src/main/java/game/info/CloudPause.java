package game.info;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CloudPause implements Screen {
    private final MyGames game;
    private final CloudScreen gameScreen;
    private Stage stage;
    private Texture background;
    private SpriteBatch batch;

    private Texture replayButtonTexture;
    private Texture replayButtonPressedTexture;
    private Texture playButtonTexture;
    private Texture playButtonPressedTexture;
    private Texture homeButtonTexture;
    private Texture homeButtonPressedTexture;

    public CloudPause(MyGames game, CloudScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        background = new Texture(Gdx.files.internal("space1.png"));
        replayButtonTexture = new Texture(Gdx.files.internal("restart01.png"));
        replayButtonPressedTexture = new Texture(Gdx.files.internal("restart02.png"));
        playButtonTexture = new Texture(Gdx.files.internal("play01.png"));
        playButtonPressedTexture = new Texture(Gdx.files.internal("play02.png"));
        homeButtonTexture = new Texture(Gdx.files.internal("home01.png"));
        homeButtonPressedTexture = new Texture(Gdx.files.internal("home02.png"));

        createButtons();
    }

    private void createButtons() {
        float buttonScale = 0.8f;
        float buttonWidth = replayButtonTexture.getWidth() * buttonScale;
        float buttonHeight = replayButtonTexture.getHeight() * buttonScale;
        float padding = 50f;
        float topMargin = 90f;
        float startY = Gdx.graphics.getHeight() - topMargin - buttonHeight;
        float centerX = Gdx.graphics.getWidth()/2 - buttonWidth/2;

        ImageButton.ImageButtonStyle replayStyle = new ImageButton.ImageButtonStyle();
        replayStyle.up = new TextureRegionDrawable(new TextureRegion(replayButtonTexture));
        replayStyle.down = new TextureRegionDrawable(new TextureRegion(replayButtonPressedTexture));
        ImageButton replayButton = new ImageButton(replayStyle);
        replayButton.setSize(buttonWidth, buttonHeight);
        replayButton.setPosition(centerX, startY);
        replayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playClickSound();
                game.setScreen(new CloudScreen(game));

            }
        });


        ImageButton.ImageButtonStyle playStyle = new ImageButton.ImageButtonStyle();
        playStyle.up = new TextureRegionDrawable(new TextureRegion(playButtonTexture));
        playStyle.down = new TextureRegionDrawable(new TextureRegion(playButtonPressedTexture));
        ImageButton playButton = new ImageButton(playStyle);
        playButton.setSize(buttonWidth, buttonHeight);
        playButton.setPosition(centerX, startY - buttonHeight - padding);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playClickSound();
                if (gameScreen != null) {
                    gameScreen.setPaused(false);
                    game.setScreen(gameScreen);
                } else {
                    Gdx.app.error("PauseScreen", "gameScreen is null!");
                    game.setScreen(new CloudScreen(game));
                }
            }
        });

        ImageButton.ImageButtonStyle homeStyle = new ImageButton.ImageButtonStyle();
        homeStyle.up = new TextureRegionDrawable(new TextureRegion(homeButtonTexture));
        homeStyle.down = new TextureRegionDrawable(new TextureRegion(homeButtonPressedTexture));
        ImageButton homeButton = new ImageButton(homeStyle);
        homeButton.setSize(buttonWidth, buttonHeight);
        homeButton.setPosition(centerX, startY - 2*(buttonHeight + padding));
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playClickSound();
                game.setScreen(new GameScreen(game));

            }
        });

        stage.addActor(replayButton);
        stage.addActor(playButton);
        stage.addActor(homeButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameScreen != null) {
            gameScreen.render(0);
        }

        batch.begin();
        batch.setColor(1, 1, 1, 1);
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(1, 1, 1, 1);
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
        if (gameScreen instanceof InputProcessor) {
            Gdx.input.setInputProcessor((InputProcessor) gameScreen);
        } else {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {

        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (batch != null) batch.dispose();
        if (background != null) background.dispose();
        if (replayButtonTexture != null) replayButtonTexture.dispose();
        if (replayButtonPressedTexture != null) replayButtonPressedTexture.dispose();
        if (playButtonTexture != null) playButtonTexture.dispose();
        if (playButtonPressedTexture != null) playButtonPressedTexture.dispose();
        if (homeButtonTexture != null) homeButtonTexture.dispose();
        if (homeButtonPressedTexture != null) homeButtonPressedTexture.dispose();
    }
}
