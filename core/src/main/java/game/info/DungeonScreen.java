package game.info;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.*;

import java.util.ArrayList;
import java.util.List;

public class DungeonScreen implements Screen, InputProcessor {
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
    private Texture bombButtonTexture;
    private Rectangle bombButtonBounds;
    private float bombCooldown = 0;
    private static final float BOMB_COOLDOWN_TIME = 0.5f;
    private Texture diamondTexture;
    private BitmapFont font;

    private int TILE_SIZE = 16;
    private int MAP_WIDTH_TILES = 30;
    private IntSet activePointers = new IntSet();
    private int MAP_HEIGHT_TILES = 30;
    public int MAP_WIDTH = MAP_WIDTH_TILES * TILE_SIZE;
    public int MAP_HEIGHT = MAP_HEIGHT_TILES * TILE_SIZE;

    private static final float PLAYER_SPEED = 120f;
    private static final float JOYSTICK_SIZE = 120f;
    private static final float JOYSTICK_MARGIN = 150f;
    private static final float BOMB_BUTTON_SIZE = 150f;
    private static final float BOMB_BUTTON_MARGIN = 150f;
    private static final float INITIAL_ZOOM = 1f;
    private static final float MONSTER_SPEED = 80f;
    private static final float BOMB_SPEED = 300f;

    private float timeSinceStart = 0;
    private int monsterCount = 0;
    private float monsterSpawnTimer = 0;
    private static final float MONSTER_SPAWN_INTERVAL = 5f;
    private Array<Monster> monsters;
    private Array<Bomb> bombs;
    private int playerHealth = 100;
    private boolean gameOver = false;

    private Texture bombTexture;
    private Texture monsterIdleTexture;
    private Texture monsterWalkTexture1;
    private Texture monsterWalkTexture2;
    private BitmapFont healthFont;
    private Texture spellTexture;
    private Spell currentSpell;
    private boolean spellCollected = false;
    private float spellSpawnTimer = 0;
    private static final float SPELL_SPAWN_DELAY = 10f;
    private static final float SPELL_RESPAWN_INTERVAL = 6f;
    private static final float SPELL_ANIMATION_SPEED = 0.5f;
    private static final float SPELL_ANIMATION_HEIGHT = 5f;
    private Texture knifeTexture;
    private Array<Knife> knives;

    private float islandMinX, islandMinY, islandMaxX, islandMaxY;

    private boolean isTransitioning = false;
    private final Object monstersLock = new Object();
    private final Object bombsLock = new Object();
    private final Object knivesLock = new Object();
    private Animation<TextureRegion> petAnimation;
    private float petAnimationTime = 0;
    private float petOffsetX = 2;
    private float petOffsetY = -20;
    private boolean petFacingRight = true;
    private float petScale = 0.8f;
    private boolean petMoving = false;
    // Pause button variables
    private Texture pauseButtonTexture1;
    private Texture pauseButtonTexture2;
    private Animation<TextureRegion> pauseButtonAnimation;
    private Rectangle pauseButtonBounds;
    private float pauseButtonAnimationTime = 0;
    private static final float PAUSE_BUTTON_WIDTH = 263;
    private static final float PAUSE_BUTTON_HEIGHT = 114;
    private static final float PAUSE_BUTTON_MARGIN = 20f;
    private boolean isPauseButtonPressed = false;
    private boolean isPaused = false;
    private Texture[] hpTextures = new Texture[5];
    private float soundTimer = 0;
    private static final float SOUND_INTERVAL = 15f;
    private int lastPlayedSoundIndex = -1;
    private int previousHealthLevel = 0;
    private static final int[] HEALTH_THRESHOLDS = {80, 60, 40, 20};
    private void setupPauseButton() {
        pauseButtonTexture1 = new Texture(Gdx.files.internal("pause01.png"));
        pauseButtonTexture2 = new Texture(Gdx.files.internal("pause02.png"));
        pauseButtonTexture1.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pauseButtonTexture2.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(pauseButtonTexture1));
        frames.add(new TextureRegion(pauseButtonTexture2));
        pauseButtonAnimation = new Animation<>(0.2f, frames);

        updatePauseButtonPosition();
    }

    private void updatePauseButtonPosition() {
        float spellX = Gdx.graphics.getWidth() - 450;
        pauseButtonBounds = new Rectangle(
            spellX - PAUSE_BUTTON_WIDTH - PAUSE_BUTTON_MARGIN,
            Gdx.graphics.getHeight() - PAUSE_BUTTON_HEIGHT - PAUSE_BUTTON_MARGIN,
            PAUSE_BUTTON_WIDTH,
            PAUSE_BUTTON_HEIGHT
        );
    }


    private void gameOver() {
        if (isTransitioning) return;
        isTransitioning = true;
        gameOver = true;

        Gdx.app.postRunnable(() -> {
            game.resetTempUpgrades();
            game.playgameOverMusic();
            game.setScreen(new GO2(game));
            dispose();
        });
    }

    public DungeonScreen(MyGames game) {
        this.game = game;
        resetGameState();

        initialize();
        this.gameCamera = new OrthographicCamera();
        this.gameViewport = new FillViewport(MyGames.WIDTH / INITIAL_ZOOM, MyGames.HEIGHT / INITIAL_ZOOM, gameCamera);
        this.uiCamera = new OrthographicCamera();
        this.uiViewport = new ScreenViewport(uiCamera);
        this.batch = new SpriteBatch();

        monsters = new Array<>();
        bombs = new Array<>();
        knives = new Array<>();

        diamondTexture = new Texture(Gdx.files.internal("stone.png"));
        spellTexture = new Texture(Gdx.files.internal("spell.png"));
        font = new BitmapFont();
        font.getData().setScale(3f);
    }

    @Override
    public void show() {
        game.playDungeonMusic();
        Gdx.app.log("DungeonScreen", "Show called");
        loadMap();
        createPlayer();
        setupJoystick();
        setupIslandBounds();
        loadTextures();
        setupBombButton();
        setupPet();
        setupPauseButton();

        healthFont = new BitmapFont();
        healthFont.getData().setScale(2f);
        healthFont.setColor(Color.WHITE);

        Gdx.input.setInputProcessor(new InputMultiplexer(this));
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    private void setupPet() {
        String pet = game.getCurrentPet();
        if (pet == null || pet.equals("none")) {
            petAnimation = null;
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
                    petAnimation = null;
                    return;
            }

            Array<TextureRegion> frames = new Array<>();
            frames.add(new TextureRegion(petTexture1));
            frames.add(new TextureRegion(petTexture2));
            petAnimation = new Animation<>(0.2f, frames, Animation.PlayMode.LOOP);
        } catch (Exception e) {
            Gdx.app.error("ForestScreen", "Pet setup failed: " + e.getMessage());
            petAnimation = null;
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


    private void setupBombButton() {
        bombButtonTexture = new Texture(Gdx.files.internal("bomb.png"));
        bombButtonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        updateBombButtonPosition();
    }

    private void updateBombButtonPosition() {
        bombButtonBounds = new Rectangle(
            Gdx.graphics.getWidth() - BOMB_BUTTON_MARGIN - BOMB_BUTTON_SIZE / 2,
            BOMB_BUTTON_MARGIN - BOMB_BUTTON_SIZE / 2,
            BOMB_BUTTON_SIZE,
            BOMB_BUTTON_SIZE
        );
    }

    private void loadTextures() {
        bombTexture = new Texture(Gdx.files.internal("bomb.png"));
        bombTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        spellTexture = new Texture(Gdx.files.internal("spell.png"));
        spellTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        monsterIdleTexture = new Texture(Gdx.files.internal("enemy.png"));
        monsterWalkTexture1 = new Texture(Gdx.files.internal("enemy1.png"));
        monsterWalkTexture2 = new Texture(Gdx.files.internal("enemy2.png"));

        knifeTexture = new Texture(Gdx.files.internal("imp/knife.png"));
        knifeTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        monsterIdleTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        monsterWalkTexture1.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        monsterWalkTexture2.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        for (int i = 0; i < 5; i++) {
            hpTextures[i] = new Texture(Gdx.files.internal("hp" + (i+1) + ".png"));
            hpTextures[i].setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    private void setupIslandBounds() {
        float[] vertices = {
            100, 100,
            100, 380,
            380, 380,
            380, 100
        };
        islandBounds = new Polygon(vertices);

        islandMinX = 100;
        islandMinY = 100;
        islandMaxX = 380;
        islandMaxY = 380;
    }

    private void loadMap() {
        try {
            tiledMap = new TmxMapLoader().load("map3/dungeon.tmx");
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            for (int i = 0; i < tiledMap.getLayers().getCount(); i++) {
                tiledMap.getLayers().get(i).getProperties().put("textureminfilter", "Nearest");
                tiledMap.getLayers().get(i).getProperties().put("texturemagfilter", "Nearest");
            }

            MAP_WIDTH_TILES = tiledMap.getProperties().get("width", Integer.class);
            MAP_HEIGHT_TILES = tiledMap.getProperties().get("height", Integer.class);
            TILE_SIZE = tiledMap.getProperties().get("tilewidth", Integer.class);
            MAP_WIDTH = MAP_WIDTH_TILES * TILE_SIZE;
            MAP_HEIGHT = MAP_HEIGHT_TILES * TILE_SIZE;

        } catch (Exception e) {
            Gdx.app.error("MAP", "Load failed: " + e.getMessage());
            tiledMap = new TiledMap();
        }
    }

    private void createPlayer() {

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

        float speed = game.isSpeedUpgradeActive() || game.isTempSpeedUpgradeActive() ? PLAYER_SPEED * 3 : PLAYER_SPEED;
        Texture[] walkTextures = {walkTexture1, walkTexture2};
        player = new Player(idleTexture, walkTextures,
            MAP_WIDTH / 2f, MAP_HEIGHT / 2f,
            speed);
    }

    private void setupJoystick() {
        Texture knob = new Texture(Gdx.files.internal("knob.png"));
        Texture base = new Texture(Gdx.files.internal("base.png"));
        knob.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        base.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        joystick = new Joystick(knob, base, JOYSTICK_MARGIN, JOYSTICK_MARGIN, JOYSTICK_SIZE / 2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameOver || player == null || tiledMapRenderer == null || batch == null) {
            return;
        }

        update(delta);
        renderGameWorld();
        renderUI();
    }

    private void update(float delta) {
        if (gameOver || isPaused) return;
        timeSinceStart += delta;
        bombCooldown -= delta;
        spellSpawnTimer += delta;

        if (playerHealth <= 0 && !isTransitioning) {
            gameOver();
            return;
        }
        soundTimer += delta;
        if (soundTimer >= SOUND_INTERVAL) {
            playRandomPeriodicSound();
            soundTimer = 0;
        }

        if (!gameOver) {
            updateKnives(delta);
            updatePlayer(delta);
            updateMonsters(delta);
            updateBombs(delta);
            updatePet(delta);
        }

        if (!spellCollected) {
            if (currentSpell == null && timeSinceStart > SPELL_SPAWN_DELAY && spellSpawnTimer >= SPELL_RESPAWN_INTERVAL) {
                spawnSpell();
                spellSpawnTimer = 0;
            }
            updateSpell(delta);
        } else {
            spellSpawnTimer += delta;
            if (spellSpawnTimer >= SPELL_RESPAWN_INTERVAL) {
                spellCollected = false;
                spellSpawnTimer = 0;
            }
        }

        if (timeSinceStart > 5f) {
            spawnMonsters(delta);
        }

        if ((game.isAutoSpellUpgradeActive() || game.isTempAutoSpellUpgradeActive()) &&
            currentSpell != null && !spellCollected) {
            game.spellsCollected++;
            game.savePreferences();
            currentSpell = null;
            spellCollected = true;
            spellSpawnTimer = 0;
        }
    }
    private void playRandomPeriodicSound() {
        List<Runnable> sounds = new ArrayList<>();
        sounds.add(() -> game.playFreezeSound());
        sounds.add(() -> game.playHealSound());
        sounds.add(() -> game.playTwisterSound());
        sounds.add(() -> game.playHellstormSound());

        if (lastPlayedSoundIndex >= 0 && sounds.size() > 1) {
            sounds.remove(lastPlayedSoundIndex);
        }

        int randomIndex = MathUtils.random(sounds.size() - 1);
        sounds.get(randomIndex).run();

        lastPlayedSoundIndex = randomIndex;
    }
    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void spawnSpell() {
        float spawnX = MathUtils.random(islandMinX, islandMaxX);
        float spawnY = MathUtils.random(islandMinY, islandMaxY);
        currentSpell = new Spell(spellTexture, spawnX, spawnY);
    }

    private void updateSpell(float delta) {
        if (currentSpell != null) {
            currentSpell.update(delta);

            if (currentSpell.getBoundingRectangle().overlaps(player.getBoundingRectangle())) {
                game.playSpellCollectSound();
                game.spellsCollected++;
                game.savePreferences();
                currentSpell = null;
                spellCollected = true;
            }
        }
    }

    private void updateKnives(float delta) {
        synchronized (knivesLock) {
            for (int i = knives.size - 1; i >= 0; i--) {
                Knife knife = knives.get(i);
                knife.update(delta);

                if (knife.isFading()) {
                    knife.setFadeTimer(knife.getFadeTimer() - delta);
                    if (knife.getFadeTimer() <= 0) {
                        knives.removeIndex(i);
                    }
                }
            }
        }
    }

    private void launchKnives() {
        game.playKnifeSound();
        synchronized (monstersLock) {
            int knivesToLaunch = (game.isKnifeUpgradeActive() || game.isTempKnifeUpgradeActive()) ? 2 : 1;

            if (monsters.size == 0) {
                for (int i = 0; i < knivesToLaunch; i++) {
                    Vector2 direction = new Vector2(i % 2 == 0 ? 1 : -1, 0);
                    Knife knife = new Knife(knifeTexture, player.getX(), player.getY(),
                        direction.x * BOMB_SPEED * 1.5f, direction.y * BOMB_SPEED * 1.5f);
                    synchronized (knivesLock) {
                        knives.add(knife);
                    }
                }
                return;
            }

            Monster nearestMonster = null;
            float minDistance = Float.MAX_VALUE;

            for (Monster monster : monsters) {
                if (monster.isFading()) continue;

                float dx = monster.getX() - player.getX();
                float dy = monster.getY() - player.getY();
                float distance = dx * dx + dy * dy;

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestMonster = monster;
                }
            }

            if (nearestMonster != null) {
                Vector2 direction = new Vector2(
                    nearestMonster.getX() - player.getX(),
                    nearestMonster.getY() - player.getY()
                ).nor();

                Knife knife = new Knife(knifeTexture, player.getX(), player.getY(),
                    direction.x * BOMB_SPEED * 1.5f, direction.y * BOMB_SPEED * 1.5f);

                synchronized (knivesLock) {
                    knives.add(knife);
                }

                if (monsters.size > 1) {
                    Monster secondMonster = null;
                    float secondMinDistance = Float.MAX_VALUE;

                    for (Monster monster : monsters) {
                        if (monster.isFading() || monster == nearestMonster) continue;

                        float dx = monster.getX() - player.getX();
                        float dy = monster.getY() - player.getY();
                        float distance = dx * dx + dy * dy;

                        if (distance < secondMinDistance) {
                            secondMinDistance = distance;
                            secondMonster = monster;
                        }
                    }

                    if (secondMonster != null) {
                        Vector2 secondDirection = new Vector2(
                            secondMonster.getX() - player.getX(),
                            secondMonster.getY() - player.getY()
                        ).nor();

                        Knife secondKnife = new Knife(knifeTexture, player.getX(), player.getY(),
                            secondDirection.x * BOMB_SPEED * 1.5f, secondDirection.y * BOMB_SPEED * 1.5f);

                        synchronized (knivesLock) {
                            knives.add(secondKnife);
                        }
                    } else {
                        Vector2 oppositeDirection = new Vector2(
                            -nearestMonster.getX() + player.getX(),
                            -nearestMonster.getY() + player.getY()
                        ).nor();

                        Knife oppositeKnife = new Knife(knifeTexture, player.getX(), player.getY(),
                            oppositeDirection.x * BOMB_SPEED * 1.5f, oppositeDirection.y * BOMB_SPEED * 1.5f);

                        synchronized (knivesLock) {
                            knives.add(oppositeKnife);
                        }
                    }
                } else {
                    Vector2 oppositeDirection = new Vector2(
                        -nearestMonster.getX() + player.getX(),
                        -nearestMonster.getY() + player.getY()
                    ).nor();

                    Knife oppositeKnife = new Knife(knifeTexture, player.getX(), player.getY(),
                        oppositeDirection.x * BOMB_SPEED * 1.5f, oppositeDirection.y * BOMB_SPEED * 1.5f);

                    synchronized (knivesLock) {
                        knives.add(oppositeKnife);
                    }
                }
            }
        }
    }

    private void spawnMonsters(float delta) {
        monsterSpawnTimer += delta;
        if (monsterSpawnTimer >= MONSTER_SPAWN_INTERVAL) {
            monsterSpawnTimer = 0;
            monsterCount++;
            for (int i = 0; i < monsterCount; i++) {
                float spawnX = MathUtils.random(0, MAP_WIDTH);
                float spawnY = MathUtils.random(0, MAP_HEIGHT);
                Monster monster = new Monster(monsterIdleTexture,
                    new Texture[]{monsterWalkTexture1, monsterWalkTexture2},
                    spawnX, spawnY, MONSTER_SPEED);

                synchronized (monstersLock) {
                    monsters.add(monster);
                }
            }
        }
    }

    private void updatePlayer(float delta) {
        Vector2 direction = joystick.getDirection();
        player.update(direction, delta, islandBounds);

        gameCamera.position.set(
            MathUtils.clamp(player.getX(), gameCamera.viewportWidth / 2, MAP_WIDTH - gameCamera.viewportWidth / 2),
            MathUtils.clamp(player.getY(), gameCamera.viewportHeight / 2, MAP_HEIGHT - gameCamera.viewportHeight / 2),
            0
        );
        gameCamera.update();
    }

    private void launchBombToNearestMonster() {
        if (game.isKnifeUpgradeActive() || game.isTempKnifeUpgradeActive()) {
            launchKnives();
            game.playKnifeSound();
            return;
        }

        synchronized (monstersLock) {
            if (monsters.size == 0) {
                Vector2 bombDirection = new Vector2(1, 0);
                if (!player.isFacingRight()) {
                    bombDirection.x = -1;
                }
                Bomb bomb = new Bomb(bombTexture, player.getX(), player.getY(),
                    bombDirection.x * BOMB_SPEED, bombDirection.y * BOMB_SPEED);

                synchronized (bombsLock) {
                    bombs.add(bomb);
                }
                game.playBombSound();
                return;
            }

            Monster nearestMonster = null;
            float minDistance = Float.MAX_VALUE;

            for (Monster monster : monsters) {
                if (monster.isFading()) continue;

                float dx = monster.getX() - player.getX();
                float dy = monster.getY() - player.getY();
                float distance = dx * dx + dy * dy;

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestMonster = monster;
                }
            }

            if (nearestMonster != null) {
                Vector2 direction = new Vector2(
                    nearestMonster.getX() - player.getX(),
                    nearestMonster.getY() - player.getY()
                ).nor();

                Bomb bomb = new Bomb(bombTexture, player.getX(), player.getY(),
                    direction.x * BOMB_SPEED, direction.y * BOMB_SPEED);

                synchronized (bombsLock) {
                    bombs.add(bomb);
                }
                game.playBombSound();
            }
        }
    }

    private void updateMonsters(float delta) {
        if (gameOver || player == null) return;

        synchronized (monstersLock) {
            for (int i = monsters.size - 1; i >= 0; i--) {
                Monster monster = monsters.get(i);
                if (monster == null) continue;

                monster.update(player.getX(), player.getY(), delta);

                if (monster.isFading()) {
                    monster.setFadeTimer(monster.getFadeTimer() - delta);
                    if (monster.getFadeTimer() <= 0) {
                        monsters.removeIndex(i);
                    }
                    continue;
                }

                if (monster.getBoundingRectangle().overlaps(player.getBoundingRectangle())) {
                    playerHealth -= 10;
                    game.playPlayerHurtSound();
                    Gdx.app.postRunnable(() -> {
                        MyGames.diamonds = Math.max(0, MyGames.diamonds - 10);
                        game.savePreferences();
                    });
                    monster.startFading(0.5f);

                    if (playerHealth <= 0 && !isTransitioning) {
                        gameOver();
                        return;
                    }
                }

                synchronized (knivesLock) {
                    for (int j = knives.size - 1; j >= 0; j--) {
                        Knife knife = knives.get(j);
                        if (monster.getBoundingRectangle().overlaps(knife.getBoundingRectangle())) {
                            monster.startFading(1f);
                            knife.startFading(0.5f);
                            game.playMonsterDeathSound();
                            Gdx.app.postRunnable(() -> {
                                MyGames.diamonds += 5 * monsterCount;
                                game.savePreferences();
                            });
                            break;
                        }
                    }
                }

                synchronized (bombsLock) {
                    for (int j = bombs.size - 1; j >= 0; j--) {
                        Bomb bomb = bombs.get(j);
                        if (monster.getBoundingRectangle().overlaps(bomb.getBoundingRectangle())) {
                            monster.startFading(1f);
                            bomb.startFading(0.5f);
                            game.playMonsterDeathSound();
                            Gdx.app.postRunnable(() -> {
                                MyGames.diamonds += 5 * monsterCount;
                                game.savePreferences();
                            });
                            break;
                        }
                    }
                }
            }
        }
    }


    private void updateBombs(float delta) {
        synchronized (bombsLock) {
            for (int i = bombs.size - 1; i >= 0; i--) {
                Bomb bomb = bombs.get(i);
                bomb.update(delta);
                if (bomb.isFading()) {
                    bomb.setFadeTimer(bomb.getFadeTimer() - delta);
                    if (bomb.getFadeTimer() <= 0) {
                        bombs.removeIndex(i);
                    }
                }
            }
        }
    }

    private void renderGameWorld() {
        gameViewport.apply();
        tiledMapRenderer.setView(gameCamera);
        tiledMapRenderer.render();

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        player.draw(batch);

        synchronized (monstersLock) {
            for (Monster monster : monsters) {
                monster.draw(batch);
            }
        }

        synchronized (bombsLock) {
            for (Bomb bomb : bombs) {
                bomb.draw(batch);
            }
        }

        synchronized (knivesLock) {
            for (Knife knife : knives) {
                knife.draw(batch);
            }
        }

        if (currentSpell != null) {
            currentSpell.draw(batch);
        }
        drawPet();
        batch.end();
    }

    private void renderUI() {
        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        TextureRegion pauseFrame;
        if (isPauseButtonPressed) {
            pauseButtonAnimationTime += Gdx.graphics.getDeltaTime();
            pauseFrame = pauseButtonAnimation.getKeyFrame(pauseButtonAnimationTime);
        } else {
            pauseFrame = new TextureRegion(pauseButtonTexture1);
        }

        batch.draw(pauseFrame,
            pauseButtonBounds.x, pauseButtonBounds.y,
            PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT);

        joystick.draw(batch);

        float alpha = bombCooldown > 0 ? 0.5f : 1f;
        batch.setColor(1, 1, 1, alpha);

        if (game.isKnifeUpgradeActive() || game.isTempKnifeUpgradeActive()) {
            float knifeWidth = knifeTexture.getWidth();
            float knifeHeight = knifeTexture.getHeight();
            float scale = BOMB_BUTTON_SIZE / knifeHeight;
            float scaledWidth = knifeWidth * scale;
            float scaledHeight = knifeHeight * scale;
            float x = bombButtonBounds.x + (bombButtonBounds.width - scaledWidth) / 2;
            float y = bombButtonBounds.y + (bombButtonBounds.height - scaledHeight) / 2;
            batch.draw(knifeTexture, x, y, scaledWidth, scaledHeight);
        } else {
            batch.draw(bombButtonTexture,
                bombButtonBounds.x, bombButtonBounds.y,
                bombButtonBounds.width, bombButtonBounds.height);
        }

        batch.setColor(1, 1, 1, 1);

        int hpIndex;
        if (playerHealth > 80) hpIndex = 0;
        else if (playerHealth > 60) hpIndex = 1;
        else if (playerHealth > 40) hpIndex = 2;
        else if (playerHealth > 20) hpIndex = 3;
        else hpIndex = 4;
        if (hpIndex != previousHealthLevel) {
            game.playLessHpSound();
            previousHealthLevel = hpIndex;
        }
        float hpWidth = 441;
        float hpHeight = 108f;

        batch.draw(hpTextures[hpIndex], 40, uiViewport.getScreenHeight() - 120, hpWidth, hpHeight);


        // healthFont.draw(batch, "HP: " + playerHealth + "%", 40, uiViewport.getScreenHeight() - 40);

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
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, true);
        uiCamera.setToOrtho(false, width, height);
        uiCamera.position.set(width / 2f, height / 2f, 0);
        uiCamera.update();
        joystick.setPosition(JOYSTICK_MARGIN, JOYSTICK_MARGIN);
        updateBombButtonPosition();
        updatePauseButtonPosition();

    }

    @Override
    public void dispose() {
        if (!isPaused) {
            if (petAnimation != null) {
                try {
                    for (TextureRegion frame : petAnimation.getKeyFrames()) {
                        if (frame != null && frame.getTexture() != null) {
                            frame.getTexture().dispose();
                        }
                    }
                } catch (Exception e) {
                    Gdx.app.error("ForestScreen", "Error disposing pet animation", e);
                }
            }

            if (tiledMap != null) tiledMap.dispose();
            if (player != null) player.dispose();
            if (joystick != null) joystick.dispose();
            if (batch != null) batch.dispose();
            if (bombTexture != null) bombTexture.dispose();
            if (monsterIdleTexture != null) monsterIdleTexture.dispose();
            if (monsterWalkTexture1 != null) monsterWalkTexture1.dispose();
            if (monsterWalkTexture2 != null) monsterWalkTexture2.dispose();
            if (bombButtonTexture != null) bombButtonTexture.dispose();
            if (healthFont != null) healthFont.dispose();
            if (diamondTexture != null) diamondTexture.dispose();
            if (spellTexture != null) spellTexture.dispose();
            if (font != null) font.dispose();
            if (knifeTexture != null) knifeTexture.dispose();
            if (pauseButtonTexture1 != null) pauseButtonTexture1.dispose();
            if (pauseButtonTexture2 != null) pauseButtonTexture2.dispose();
            for (Texture hpTexture : hpTextures) {
                if (hpTexture != null) hpTexture.dispose();
            }
            tiledMap = null;
            tiledMapRenderer = null;
            player = null;
            joystick = null;
            batch = null;

        }}

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 touchPos = new Vector3(screenX, screenY, 0);
        uiViewport.unproject(touchPos);
        activePointers.add(pointer);

        // Обработка кнопки паузы
        if (pauseButtonBounds.contains(touchPos.x, touchPos.y)) {
            game.playClickSound();
            isPauseButtonPressed = true;
            pauseButtonAnimationTime = 0;
            return true;
        }

        // Обработка кнопки стрельбы
        if (bombButtonBounds.contains(touchPos.x, touchPos.y)) {
            if (bombCooldown <= 0) {
                launchBombToNearestMonster();
                bombCooldown = BOMB_COOLDOWN_TIME;
            }
            return true;
        }

        // Обработка джойстика
        if (pointer == 0) {
            joystick.handleTouch(touchPos.x, touchPos.y, true);
        }

        return true;
    }

    private class Knife {
        private float x, y;
        private float xVelocity, yVelocity;
        private Texture texture;
        private boolean fading = false;
        private float fadeTimer = 0;
        private float alpha = 1f;
        private float animationTimer = 0;

        public Knife(Texture texture, float x, float y, float xVelocity, float yVelocity) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.xVelocity = xVelocity;
            this.yVelocity = yVelocity;
        }

        public void update(float delta) {
            animationTimer += delta;
            if (!fading) {
                x += xVelocity * delta;
                y += yVelocity * delta;
            }
        }

        public void draw(Batch batch) {
            if (fading) {
                alpha = fadeTimer;
                batch.setColor(1, 1, 1, alpha);
            }
            batch.draw(texture, x - texture.getWidth() / 2f, y - texture.getHeight() / 2f);
            if (fading) {
                batch.setColor(1, 1, 1, 1);
            }
        }

        public Rectangle getBoundingRectangle() {
            return new Rectangle(x - texture.getWidth() / 2f,
                y - texture.getHeight() / 2f,
                texture.getWidth(),
                texture.getHeight());
        }

        public void startFading(float duration) {
            fading = true;
            fadeTimer = duration;
        }

        public boolean isFading() {
            return fading;
        }
        public float getFadeTimer() {
            return fadeTimer;
        }
        public void setFadeTimer(float fadeTimer) {
            this.fadeTimer = fadeTimer;
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 touchPos = new Vector3(screenX, screenY, 0);
        uiViewport.unproject(touchPos);

        // Если это первое касание (джойстик)
        if (pointer == 0) {
            joystick.handleTouch(touchPos.x, touchPos.y, false);
        }

        return true;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 touchPos = new Vector3(screenX, screenY, 0);
        uiViewport.unproject(touchPos);
        activePointers.remove(pointer);

        if (isPauseButtonPressed && pauseButtonBounds.contains(touchPos.x, touchPos.y)) {
            Gdx.app.log("PauseButton", "Pause button pressed");
            this.setPaused(true);
            game.setScreen(new PS2(game, this));
            isPauseButtonPressed = false;
            return true;
        }

        if (pointer == 0) {
            joystick.reset();
        }

        isPauseButtonPressed = false;
        return true;
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() { }
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    private class Monster {
        private float x, y;
        private float speed;
        private Texture idleTexture;
        private Texture[] walkTextures;
        private float animationTimer;
        private static final float FRAME_DURATION = 0.25f;
        private boolean fading = false;
        private float fadeTimer = 0;
        private float alpha = 1f;

        public Monster(Texture idleTexture, Texture[] walkTextures, float x, float y, float speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.idleTexture = idleTexture;
            this.walkTextures = walkTextures;
            this.animationTimer = 0;
        }

        public void update(float playerX, float playerY, float delta) {
            if (!fading) {
                float angle = MathUtils.atan2(playerY - y, playerX - x);
                x += MathUtils.cos(angle) * speed * delta;
                y += MathUtils.sin(angle) * speed * delta;
                animationTimer += delta;
            }
        }

        public void draw(Batch batch) {
            if (fading) {
                alpha = fadeTimer;
                batch.setColor(1, 1, 1, alpha);
            }

            TextureRegion frame = walkTextures != null && walkTextures.length > 0 ?
                getAnimationTexture() : new TextureRegion(idleTexture);

            batch.draw(frame, x - frame.getRegionWidth() / 2f, y - frame.getRegionHeight() / 2f);

            if (fading) {
                batch.setColor(1, 1, 1, 1);
            }
        }

        private TextureRegion getAnimationTexture() {
            int frameIndex = (int)(animationTimer / FRAME_DURATION) % walkTextures.length;
            return new TextureRegion(walkTextures[frameIndex]);
        }

        public Rectangle getBoundingRectangle() {
            return new Rectangle(x - idleTexture.getWidth() / 2f,
                y - idleTexture.getHeight() / 2f,
                idleTexture.getWidth(),
                idleTexture.getHeight());
        }

        public float getX() {
            return x;
        }
        public float getY() {
            return y;
        }

        public void startFading(float duration) {
            fading = true;
            fadeTimer = duration;
        }

        public boolean isFading() {
            return fading;
        }
        public float getFadeTimer() {
            return fadeTimer;
        }
        public void setFadeTimer(float fadeTimer) {
            this.fadeTimer = fadeTimer;
        }

        public void dispose() {
            idleTexture.dispose();
            for (Texture texture : walkTextures) {
                texture.dispose();
            }
        }
    }

    private class Bomb {
        private float x, y;
        private float xVelocity, yVelocity;
        private Texture texture;
        private boolean fading = false;
        private float fadeTimer = 0;
        private float alpha = 1f;
        private float animationTimer = 0;

        public Bomb(Texture texture, float x, float y, float xVelocity, float yVelocity) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.xVelocity = xVelocity;
            this.yVelocity = yVelocity;
        }

        public void update(float delta) {
            animationTimer += delta;
            y += MathUtils.sin(animationTimer * SPELL_ANIMATION_SPEED) * SPELL_ANIMATION_HEIGHT * delta;
            if (!fading) {
                x += xVelocity * delta;
                y += yVelocity * delta;
            }
        }

        public void draw(Batch batch) {
            if (fading) {
                alpha = fadeTimer;
                batch.setColor(1, 1, 1, alpha);
            }
            batch.draw(texture, x - texture.getWidth() / 2f, y - texture.getHeight() / 2f);
            if (fading) {
                batch.setColor(1, 1, 1, 1);
            }
        }

        public Rectangle getBoundingRectangle() {
            return new Rectangle(x - texture.getWidth() / 2f,
                y - texture.getHeight() / 2f,
                texture.getWidth(),
                texture.getHeight());
        }

        public void startFading(float duration) {
            fading = true;
            fadeTimer = duration;
        }

        public boolean isFading() {
            return fading;
        }
        public float getFadeTimer() {
            return fadeTimer;
        }
        public void setFadeTimer(float fadeTimer) {
            this.fadeTimer = fadeTimer;
        }
    }

    private void resetGameState() {
        playerHealth = 100;
        timeSinceStart = 0;
        monsterCount = 0;
        monsterSpawnTimer = 0;
        spellCollected = false;
        spellSpawnTimer = 0;
        currentSpell = null;
        if (monsters != null) monsters.clear();
        if (bombs != null) bombs.clear();
        if (knives != null) knives.clear();
    }

    private void initialize() {
        this.gameCamera = new OrthographicCamera();
        this.gameViewport = new FillViewport(MyGames.WIDTH / INITIAL_ZOOM, MyGames.HEIGHT / INITIAL_ZOOM, gameCamera);
        this.uiCamera = new OrthographicCamera();
        this.uiViewport = new ScreenViewport(uiCamera);
        this.batch = new SpriteBatch();

        monsters = new Array<>();
        bombs = new Array<>();
        knives = new Array<>();
        diamondTexture = new Texture(Gdx.files.internal("stone.png"));
        spellTexture = new Texture(Gdx.files.internal("spell.png"));
        font = new BitmapFont();
        font.getData().setScale(3f);
    }

    private class Spell {
        private float x, y;
        private final float width, height;
        private Texture texture;
        private float animationTimer = 0;

        public Spell(Texture texture, float x, float y) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.width = 15;
            this.height = 15;
        }

        public void update(float delta) {
            animationTimer += delta;
            y += MathUtils.sin(animationTimer * SPELL_ANIMATION_SPEED) * SPELL_ANIMATION_HEIGHT * delta;
        }

        public void draw(Batch batch) {
            batch.draw(texture, x - width / 2f, y - height / 2f, width, height);
        }

        public Rectangle getBoundingRectangle() {
            return new Rectangle(x - width / 2f, y - height / 2f, width, height);
        }
    }
}
