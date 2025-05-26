package game.info;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class GameScreen implements Screen, InputProcessor {
    private final MyGames game;
    private OrthographicCamera gameCamera;
    private FillViewport gameViewport;
    private OrthographicCamera uiCamera;
    private ScreenViewport uiViewport;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Player player;
    private Joystick joystick;
    private SpriteBatch batch;
    private Polygon islandBounds;

    // UI Elements
    private Stage uiStage;
    private ImageButton backButton;
    private Texture backBtnNormal;
    private Texture backBtnPressed;
    private Texture diamondTexture;
    private BitmapFont font;
    private Texture spellTexture;

    // Pet system
    private Animation<TextureRegion> petAnimation;
    private float petAnimationTime = 0;
    private float petOffsetX = 2; // Even closer!
    private float petOffsetY = -20;
    private boolean petFacingRight = true;
    private float petScale = 0.8f;
    private boolean petMoving = false; // Add this flag


    // Game objects
    private Door door;
    private Chest chest;

    // Constants
    private static final int TILE_SIZE = 16;
    private static final int MAP_WIDTH_TILES = 30;
    private static final int MAP_HEIGHT_TILES = 30;
    public static final int MAP_WIDTH = MAP_WIDTH_TILES * TILE_SIZE;
    public static final int MAP_HEIGHT = MAP_HEIGHT_TILES * TILE_SIZE;
    private static final float PLAYER_SPEED = 120f;
    private static final float JOYSTICK_SIZE = 120f;
    private static final float JOYSTICK_MARGIN = 150f;
    private static final float INITIAL_ZOOM = 1f;
    private static final float BACK_BUTTON_SCALE = 0.5f;
    private static final float DOOR_X = 250;
    private static final float DOOR_Y = 250;
    private static final float CHEST_X = 350;
    private static final float CHEST_Y = 250;
    private Animation<TextureRegion> knightAnimation;
    private float knightAnimationTime = 0;
    private float knightX = DOOR_X - 100;
    private float knightY = DOOR_Y;
    private Rectangle knightBounds;
    private Texture hiTexture;

    public GameScreen(MyGames game) {
        this.game = game;
        initialize();
    }

    private void initialize() {
        gameCamera = new OrthographicCamera();
        gameViewport = new FillViewport(MyGames.WIDTH / INITIAL_ZOOM, MyGames.HEIGHT / INITIAL_ZOOM, gameCamera);
        uiCamera = new OrthographicCamera();
        uiViewport = new ScreenViewport(uiCamera);
        batch = new SpriteBatch();
        uiStage = new Stage(new ScreenViewport(uiCamera), batch);
        hiTexture = new Texture(Gdx.files.internal("hi.png"));

        // Load UI assets
        spellTexture = new Texture(Gdx.files.internal("spell.png"));
        diamondTexture = new Texture(Gdx.files.internal("stone.png"));
        font = new BitmapFont();
        font.getData().setScale(3f);
    }

    @Override
    public void show() {
        try {
            game.playDefaultMusic();
            loadMap();
            createPlayer();
            setupJoystick();
            setupMapBounds();
            setupBackButton();
            setupPet();

            Texture knightTexture1 = new Texture(Gdx.files.internal("kn1.png"));
            Texture knightTexture2 = new Texture(Gdx.files.internal("kn3.png"));

            Array<TextureRegion> knightFrames = new Array<>();
            knightFrames.add(new TextureRegion(knightTexture1));
            knightFrames.add(new TextureRegion(knightTexture2));

            knightAnimation = new Animation<>(0.9f, knightFrames);
            knightBounds = new Rectangle(knightX, knightY,
                knightTexture1.getWidth(),
                knightTexture1.getHeight());


            InputMultiplexer multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(uiStage);
            multiplexer.addProcessor(this);
            Gdx.input.setInputProcessor(multiplexer);
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            game.resetActiveUpgrades();

            door = new Door(DOOR_X, DOOR_Y);
            chest = new Chest(CHEST_X, CHEST_Y);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Show failed: " + e.getMessage());
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void setupPet() {
        String pet = game.getCurrentPet();
        if (pet == null || pet.equals("none")) {
            return;
        }

        try {
            Texture petTexture1, petTexture2;
            switch (pet) {
                case "pet1":
                    petTexture1 = new Texture(Gdx.files.internal("idle01.png"));
                    petTexture2 = new Texture(Gdx.files.internal("idle02.png"));
                    break;
                case "pet2":
                    petTexture1 = new Texture(Gdx.files.internal("goblin01.png"));
                    petTexture2 = new Texture(Gdx.files.internal("goblin02.png"));
                    break;
                case "pet3":
                    petTexture1 = new Texture(Gdx.files.internal("zombie01.png"));
                    petTexture2 = new Texture(Gdx.files.internal("zombie02.png"));
                    break;
                default:
                    return;
            }

            Array<TextureRegion> frames = new Array<>();
            frames.add(new TextureRegion(petTexture1));
            frames.add(new TextureRegion(petTexture2));
            petAnimation = new Animation<>(0.2f, frames, Animation.PlayMode.LOOP);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Pet setup failed: " + e.getMessage());
        }
    }

    private void loadMap() {
        try {
            String mapPath = game.getCurrentMap();
            if (mapPath == null || mapPath.isEmpty()) {
                mapPath = "map1/map1.tmx";
                game.setCurrentMap(mapPath);
            }

            tiledMap = new TmxMapLoader().load(mapPath);
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

            for (int i = 0; i < tiledMap.getLayers().getCount(); i++) {
                tiledMap.getLayers().get(i).getProperties().put("textureminfilter", "Nearest");
                tiledMap.getLayers().get(i).getProperties().put("texturemagfilter", "Nearest");
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Map loading failed: " + e.getMessage());
            try {
                tiledMap = new TmxMapLoader().load("map1/map1.tmx");
                tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            } catch (Exception fallbackEx) {
                throw new RuntimeException("Fallback map loading failed", fallbackEx);
            }
        }
    }

    private void setupMapBounds() {
        try {
            String currentMap = game.getCurrentMap();
            if (currentMap == null) {
                currentMap = "map1/map1.tmx";
                game.setCurrentMap(currentMap);
            }

            if (currentMap.contains("iland")) {
                setupIslandBounds();
            } else if (currentMap.contains("china")) {
                setupChinaBounds();
            } else {
                setupDefaultBounds();
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Map bounds setup failed: " + e.getMessage());
            setupDefaultBounds();
        }
    }

    private void setupDefaultBounds() {
        float[] vertices = {0, 0, 0, MAP_HEIGHT, MAP_WIDTH, MAP_HEIGHT, MAP_WIDTH, 0};
        islandBounds = new Polygon(vertices);
    }

    private void setupIslandBounds() {
        float[] vertices = {100, 100, 100, 380, 380, 380, 380, 100};
        islandBounds = new Polygon(vertices);
    }

    private void setupChinaBounds() {
        float[] vertices = {50, 50, 50, 430, 430, 430, 430, 50};
        islandBounds = new Polygon(vertices);
    }

    private void createPlayer() {
        try {
            String hero = game.getCurrentHero();
            Texture idleTexture, walkTexture1, walkTexture2;

            switch (hero) {
                case "man":
                    idleTexture = new Texture(Gdx.files.internal("man.png"));
                    walkTexture1 = new Texture(Gdx.files.internal("man1.png"));
                    walkTexture2 = new Texture(Gdx.files.internal("man2.png"));
                    break;
                case "dragon":
                    idleTexture = new Texture(Gdx.files.internal("dragon.png"));
                    walkTexture1 = new Texture(Gdx.files.internal("dragon1.png"));
                    walkTexture2 = new Texture(Gdx.files.internal("dragon3.png"));
                    break;
                case "girl":
                    idleTexture = new Texture(Gdx.files.internal("girl.png"));
                    walkTexture1 = new Texture(Gdx.files.internal("girl1.png"));
                    walkTexture2 = new Texture(Gdx.files.internal("girl2.png"));
                    break;
                default:
                    idleTexture = new Texture(Gdx.files.internal("player.png"));
                    walkTexture1 = new Texture(Gdx.files.internal("player1.png"));
                    walkTexture2 = new Texture(Gdx.files.internal("player2.png"));
            }

            idleTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            walkTexture1.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            walkTexture2.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            Texture[] walkTextures = {walkTexture1, walkTexture2};
            player = new Player(idleTexture, walkTextures,
                MAP_WIDTH / 2f, MAP_HEIGHT / 2f,
                PLAYER_SPEED);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Player creation failed: " + e.getMessage());
            throw new RuntimeException("Player creation failed", e);
        }
    }

    private void setupJoystick() {
        try {
            Texture knob = new Texture(Gdx.files.internal("knob.png"));
            Texture base = new Texture(Gdx.files.internal("base.png"));
            knob.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            base.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            joystick = new Joystick(knob, base, JOYSTICK_MARGIN, JOYSTICK_MARGIN, JOYSTICK_SIZE / 2);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Joystick setup failed: " + e.getMessage());
        }
    }

    private void setupBackButton() {
        try {
            backBtnNormal = new Texture(Gdx.files.internal("back01.png"));
            backBtnPressed = new Texture(Gdx.files.internal("back02.png"));

            ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
            buttonStyle.up = new TextureRegionDrawable(backBtnNormal);
            buttonStyle.down = new TextureRegionDrawable(backBtnPressed);
            buttonStyle.checked = new TextureRegionDrawable(backBtnPressed);

            backButton = new ImageButton(buttonStyle);
            backButton.getImage().setScale(BACK_BUTTON_SCALE);
            backButton.setSize(backBtnNormal.getWidth() * BACK_BUTTON_SCALE,
                backBtnNormal.getHeight() * BACK_BUTTON_SCALE);

            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.playClickSound();
                    game.setScreen(new MainMenuScreen(game));
                }
            });

            uiStage.addActor(backButton);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Back button setup failed: " + e.getMessage());
        }
    }

    @Override
    public void render(float delta) {
        try {
            Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            updatePlayer(delta);
            updatePet(delta);
            knightAnimationTime += delta;
            renderGameWorld();

            batch.setProjectionMatrix(gameCamera.combined);
            batch.begin();
            door.draw(batch);
            chest.draw(batch);
            drawPet();



            TextureRegion currentKnightFrame = knightAnimation.getKeyFrame(knightAnimationTime, true);
            batch.draw(currentKnightFrame, knightX, knightY);
            /*  if (hiTexture != null) {
                float hiX = knightX + (knightBounds.width - hiTexture.getWidth()) / 2;
                float hiY = knightY + knightBounds.height - 3;
                batch.draw(hiTexture, hiX, hiY);
            }*/

            batch.end();

            knightAnimationTime += delta;

            renderUI();
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Render failed: " + e.getMessage());
        }
    }

    private void updatePlayer(float delta) {
        if (player != null && joystick != null) {
            Vector2 direction = joystick.getDirection();
            player.update(direction, delta, islandBounds);

            gameCamera.position.set(
                MathUtils.clamp(player.getX(), gameCamera.viewportWidth / 2, MAP_WIDTH - gameCamera.viewportWidth / 2),
                MathUtils.clamp(player.getY(), gameCamera.viewportHeight / 2, MAP_HEIGHT - gameCamera.viewportHeight / 2),
                0
            );
            gameCamera.update();
        }
    }

    private void updatePet(float delta) {
        if (petAnimation != null && player != null) {
            Vector2 direction = joystick.getDirection();

            if (direction.isZero(0.1f)) {
                petMoving = false;
            } else {
                petMoving = true;
            }

            if (direction.x > 0) {
                petFacingRight = true;
            } else if (direction.x < 0) {
                petFacingRight = false;
            }

            if (petMoving) {
                petAnimationTime += delta;
            }
        }
    }

    private void drawPet() {

        if (petAnimation != null && player != null && batch != null) {
            TextureRegion currentFrame = petAnimation.getKeyFrame(petAnimationTime);

            float petX = player.getX() + (petFacingRight ? petOffsetX : -petOffsetX);
            float petY = player.getY() + petOffsetY;
            float petWidth = currentFrame.getRegionWidth() * petScale;
            float petHeight = currentFrame.getRegionHeight() * petScale;

            if (!petFacingRight) {
                batch.draw(currentFrame,
                    petX + petWidth, petY,
                    -petWidth, petHeight);
            } else {
                batch.draw(currentFrame,
                    petX, petY,
                    petWidth, petHeight);
            }
        }
    }

    private void renderGameWorld() {
        if (gameViewport != null && tiledMapRenderer != null && gameCamera != null) {
            gameViewport.apply();
            tiledMapRenderer.setView(gameCamera);
            tiledMapRenderer.render();

            batch.setProjectionMatrix(gameCamera.combined);
            batch.begin();
            if (player != null) {
                player.draw(batch);
            }
            batch.end();
        }
    }

    private void renderUI() {
        if (uiViewport != null && uiCamera != null && batch != null) {
            uiViewport.apply();
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();

            if (joystick != null) {
                joystick.draw(batch);
            }

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

            if (uiStage != null) {
                uiStage.act(Gdx.graphics.getDeltaTime());
                uiStage.draw();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        if (gameViewport != null) {
            gameViewport.update(width, height, true);
        }
        if (uiViewport != null) {
            uiViewport.update(width, height, true);
        }
        if (uiCamera != null) {
            uiCamera.setToOrtho(false, width, height);
            uiCamera.position.set(width/2f, height/2f, 0);
            uiCamera.update();
        }
        if (joystick != null) {
            joystick.setPosition(JOYSTICK_MARGIN, JOYSTICK_MARGIN);
        }
        if (backButton != null && backBtnNormal != null) {
            float buttonWidth = backBtnNormal.getWidth() * BACK_BUTTON_SCALE;
            float buttonHeight = backBtnNormal.getHeight() * BACK_BUTTON_SCALE;
            backButton.setPosition((width - buttonWidth) / 2, 10);
        }
    }

    @Override
    public void dispose() {
        try {
            if (petAnimation != null) {
                for (TextureRegion frame : petAnimation.getKeyFrames()) {
                    if (frame != null && frame.getTexture() != null) {
                        frame.getTexture().dispose();
                    }
                }
            }
            if (hiTexture != null) hiTexture.dispose();
            // Dispose other resources
            if (tiledMap != null) tiledMap.dispose();
            if (player != null) player.dispose();
            if (joystick != null) joystick.dispose();
            if (batch != null) batch.dispose();
            if (door != null) door.dispose();
            if (chest != null) chest.dispose();
            if (uiStage != null) uiStage.dispose();
            if (backBtnNormal != null) backBtnNormal.dispose();
            if (backBtnPressed != null) backBtnPressed.dispose();
            if (diamondTexture != null) diamondTexture.dispose();
            if (spellTexture != null) spellTexture.dispose();
            if (font != null) font.dispose();
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Dispose failed: " + e.getMessage());
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        try {
            Vector3 touchPosUI = new Vector3(screenX, screenY, 0);
            if (uiViewport != null) {
                uiViewport.unproject(touchPosUI);
            }

            if (uiStage != null && uiStage.hit(touchPosUI.x, touchPosUI.y, true) != null) {
                return uiStage.touchDown(screenX, screenY, pointer, button);
            }

            if (joystick != null && joystick.handleTouch(touchPosUI.x, touchPosUI.y, true)) {
                return true;
            }

            Vector3 touchPosGame = new Vector3(screenX, screenY, 0);
            if (gameCamera != null) {
                gameCamera.unproject(touchPosGame);
            }

            // Проверка нажатия на рыцаря
            if (knightBounds != null && knightBounds.contains(touchPosGame.x, touchPosGame.y)) {
                game.playClickSound();
                game.setScreen(new AwardsScreen(game));
                return true;
            }

            if (door != null && door.getBounds().contains(touchPosGame.x, touchPosGame.y)) {
                game.playClickSound();
                game.setScreen(new LevelsScreen(game));
                return true;
            }

            if (chest != null && chest.getBounds().contains(touchPosGame.x, touchPosGame.y)) {
                game.playClickSound();
                game.setScreen(new ShopScreen(game));
                return true;
            }

            return false;
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Touch down failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        try {
            Vector3 touchPosUI = new Vector3(screenX, screenY, 0);
            if (uiViewport != null) {
                uiViewport.unproject(touchPosUI);
            }
            return joystick != null && joystick.handleTouch(touchPosUI.x, touchPosUI.y, false);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Touch dragged failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (joystick != null) {
            joystick.reset();
        }
        return false;
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}
