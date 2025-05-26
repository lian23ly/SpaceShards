package game.info;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MyGames extends ApplicationAdapter {
    private SpriteBatch batch;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Screen currentScreen;

    public static final int WIDTH = 320;
    public static final int HEIGHT = 240;
    public static final int SCALE = 3;
    private float volume = 1.0f;
    public static int diamonds = 0;
    private String currentMap = "map1/map1.tmx";
    private boolean islandPurchased = false;

    private ShopScreen shopScreen;
    private HeroesScreen heroesScreen;
    private MapsScreen mapsScreen;
    private ImprovementsScreen improvementsScreen;
    private String currentHero = "default";
    public static final int CHINA_MAP_SPELL_PRICE = 100;
    private boolean speedUpgradeActive = false;
    private boolean autoSpellUpgradeActive = false;
    private boolean knifeUpgradeActive = false;
    public boolean isKnifeUpgradeActive = false;
    private boolean lastLevelWasForest = false;

    private Array<Music> playlist;
    private int currentTrackIndex = 0;
    private Music currentTrack;
    private boolean isPlayingSpecialTrack = false;
    public boolean isSpeedUpgradeActive = false;
    public boolean isAutoSpellUpgradeActive = false;

    private static MyGames instance;
    boolean tempSpeedUpgradeActive = false;
    boolean tempAutoSpellUpgradeActive = false;
    boolean tempKnifeUpgradeActive = false;
    private String currentPet = "none";
    private Set<String> purchasedPets;
    private Set<String> purchasedHeroes;
    private static int totalMonstersKilled = 0;
    private Sound clickSound;
    private Sound bombSound;
    private Sound knifeSound;
    private boolean isHousePurchased = false;
    // В класс MyGames добавьте:
    private Sound spellCollectSound;
    private Sound monsterDeathSound;
    private Sound playerHurtSound;
    private Sound lessHpSound;
    private Sound gameOverMusic;
    private Sound freezeSound;
    private Sound healSound;
    private Sound twisterSound;
    private Sound hellstormSound;

    public void playgameOverMusic() {
        gameOverMusic.play(volume);
    }
    public void playLessHpSound() {
        lessHpSound.play(volume);
    }
    public void playPlayerHurtSound() {
        playerHurtSound.play(volume);
    }
    public void playSpellCollectSound() {
        spellCollectSound.play(volume);
    }


    private boolean isChinaPurchased = false;


    public boolean isChinaPurchased() {
        return isChinaPurchased;
    }

    public void setChinaPurchased(boolean purchased) {
        this.isChinaPurchased = purchased;
        savePreferences();
    }
    public boolean isHousePurchased() {
        return isHousePurchased;
    }

    public void setHousePurchased(boolean purchased) {
        this.isHousePurchased = purchased;
        savePreferences();
    }

    public void pauseCurrentTrack() {
        if (currentTrack != null && currentTrack.isPlaying()) {
            currentTrack.pause();
        }
    }

    public void resumeCurrentTrack() {
        if (currentTrack != null && musicEnabled && !currentTrack.isPlaying()) {
            currentTrack.play();
        }
    }

    public static void addMonstersKilled(int count) {
        totalMonstersKilled += count;
        Preferences prefs = Gdx.app.getPreferences("GameStats");
        prefs.putInteger("monstersKilled", totalMonstersKilled);
        prefs.flush();
    }

    public static int getMonstersKilled() {
        Preferences prefs = Gdx.app.getPreferences("GameStats");
        return prefs.getInteger("monstersKilled", 0);
    }
    public void removeCurrentPet() {
        this.currentPet = "none";
        savePreferences();
    }

    public static void incrementMonstersKilled() {
        totalMonstersKilled++;
        Preferences prefs = Gdx.app.getPreferences("GameStats");
        prefs.putInteger("monstersKilled", totalMonstersKilled);
        prefs.flush();
    }

    public static int getTotalMonstersKilled() {
        Preferences prefs = Gdx.app.getPreferences("GameStats");
        return prefs.getInteger("monstersKilled", 0);
    }
    public Set<String> getPurchasedHeroes() {
        if (purchasedHeroes == null) {
            Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
            String heroesString = prefs.getString("purchasedHeroes", "default");
            purchasedHeroes = new HashSet<>(Arrays.asList(heroesString.split(",")));
        }
        return purchasedHeroes;
    }

    public void addPurchasedHero(String hero) {
        getPurchasedHeroes().add(hero);
        savePreferences();
    }

    public void setCurrentPet(String pet) {
        this.currentPet = pet;
        savePreferences();
    }

    public Set<String> getPurchasedPets() {
        if (purchasedPets == null) {
            Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
            String petString = prefs.getString("purchasedPets", "");
            purchasedPets = new HashSet<>();
            if (!petString.isEmpty()) {
                String[] pets = petString.split(",");
                for (String pet : pets) {
                    purchasedPets.add(pet);
                }
            }
        }
        return purchasedPets;
    }

    public void setPurchasedPets(Set<String> purchasedPets) {
        this.purchasedPets = purchasedPets;
    }

    public boolean isTempSpeedUpgradeActive() {
        return tempSpeedUpgradeActive;
    }

    public boolean isTempAutoSpellUpgradeActive() {
        return tempAutoSpellUpgradeActive;
    }

    public boolean isTempKnifeUpgradeActive() {
        return tempKnifeUpgradeActive;
    }

    public void activateTempSpeedUpgrade() {
        this.tempSpeedUpgradeActive = true;
    }

    public void activateTempKnifeUpgrade() {
        this.tempKnifeUpgradeActive = true;
    }

    public void activateTempAutoSpellUpgrade() {
        this.tempAutoSpellUpgradeActive = true;
    }

    public void resetTempUpgrades() {
        tempSpeedUpgradeActive = false;
        tempAutoSpellUpgradeActive = false;
        tempKnifeUpgradeActive = false;
    }

    public static MyGames getInstance() {
        return instance;
    }

    public boolean isSpeedUpgradeActive() {
        return speedUpgradeActive;
    }

    public void activateSpeedUpgrade() {
        this.speedUpgradeActive = true;
        savePreferences();
    }

    public void deactivateSpeedUpgrade() {
        this.speedUpgradeActive = false;
        savePreferences();
    }

    public boolean isAutoSpellUpgradeActive() {
        return autoSpellUpgradeActive;
    }

    public void activateAutoSpellUpgrade() {
        isAutoSpellUpgradeActive = true;
    }

    public void deactivateAutoSpellUpgrade() {
        this.autoSpellUpgradeActive = false;
        savePreferences();
    }

    public boolean isKnifeUpgradeActive() {
        return knifeUpgradeActive;
    }

    public void activateKnifeUpgrade() {
        this.knifeUpgradeActive = true;
        savePreferences();
    }

    public void deactivateKnifeUpgrade() {
        this.knifeUpgradeActive = false;
        savePreferences();
    }

    public String getCurrentHero() {
        return currentHero;
    }

    public void setCurrentHero(String hero) {
        this.currentHero = hero;
        savePreferences();
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        updateMusicVolume();
        savePreferences();
    }

    public String getCurrentPet() {
        return currentPet;
    }

    public void loadPlaylist() {
        if (playlist != null) {
            for (Music track : playlist) {
                track.dispose();
            }
        }

        playlist = new Array<>();
        try {
            playlist.add(Gdx.audio.newMusic(Gdx.files.internal("heat_waves.mp3")));
            playlist.add(Gdx.audio.newMusic(Gdx.files.internal("xo.mp3")));
            playlist.add(Gdx.audio.newMusic(Gdx.files.internal("chiphead64.mp3")));

            for (Music track : playlist) {
                track.setLooping(false);
            }

            Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
            currentTrackIndex = prefs.getInteger("lastTrackIndex", 0);

            currentTrack = playlist.get(currentTrackIndex);
            currentTrack.setVolume(volume);
            currentTrack.setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    playNextTrack();
                }
            });

        } catch (Exception e) {
            Gdx.app.error("MyGames", "Ошибка при загрузке музыки: " + e.getMessage(), e);
        }
    }

    public void playNextTrack() {
        if (playlist == null || playlist.size == 0 || !musicEnabled) {
            return;
        }

        if (isPlayingSpecialTrack) return;

        currentTrackIndex = (currentTrackIndex + 1) % playlist.size;
        if (currentTrack != null) {
            currentTrack.stop();
        }

        currentTrack = playlist.get(currentTrackIndex);
        currentTrack.setVolume(volume);
        currentTrack.play();

        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        prefs.putInteger("lastTrackIndex", currentTrackIndex);
        prefs.flush();
    }

    public void updateMusicVolume() {
        if (currentTrack != null) {
            float boostedVolume = volume * 100.0f;
            currentTrack.setVolume(Math.min(1.0f, boostedVolume));
        }
    }

    public void stopMusic() {
        if (currentTrack != null) {
            currentTrack.stop();
        }
    }

    public void pauseMusic() {
        if (currentTrack != null) {
            currentTrack.pause();
        }
    }

    public void resumeMusic() {
        if (currentTrack != null && musicEnabled) {
            currentTrack.play();
        }
    }

    public String getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(String mapPath) {
        this.currentMap = mapPath;
    }

    public boolean isIslandPurchased() {
        return islandPurchased;
    }

    public static int spellsCollected = 0;

    public void setIslandPurchased(boolean purchased) {
        this.islandPurchased = purchased;
        savePreferences();
    }

    private boolean musicEnabled = true;

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void toggleMusic() {
        musicEnabled = !musicEnabled;
        if (musicEnabled) {
            resumeMusic();
        } else {
            pauseMusic();
        }
        savePreferences();
    }

    public void playForestMusic() {
        if (!musicEnabled) return;
        if (isPlayingSpecialTrack && currentTrack != null && currentTrack.isPlaying()) {
            return;
        }
        isPlayingSpecialTrack = true;
        stopMusic();
        try {
            currentTrack = Gdx.audio.newMusic(Gdx.files.internal("tamed_dashed.mp3"));
            currentTrack.setLooping(true);
            currentTrack.setVolume(volume);
            currentTrack.play();
        } catch (Exception e) {
            Gdx.app.error("MyGames", "Error loading forest music: " + e.getMessage());
        }
    }

    public void playDungeonMusic() {
        if (!musicEnabled) return;
        if (isPlayingSpecialTrack && currentTrack != null && currentTrack.isPlaying()) {
            return;
        }
        isPlayingSpecialTrack = true;
        stopMusic();
        try {
            currentTrack = Gdx.audio.newMusic(Gdx.files.internal("drunk_dazed.mp3"));
            currentTrack.setLooping(true);
            currentTrack.setVolume(volume);
            currentTrack.play();
        } catch (Exception e) {
            Gdx.app.error("MyGames", "Error loading dungeon music: " + e.getMessage());
        }
    }

    public void playCloudMusic() {
        if (!musicEnabled) return;
        if (isPlayingSpecialTrack && currentTrack != null && currentTrack.isPlaying()) {
            return;
        }
        isPlayingSpecialTrack = true;
        stopMusic();
        try {
            currentTrack = Gdx.audio.newMusic(Gdx.files.internal("blessed_cursed.mp3"));
            currentTrack.setLooping(true);
            currentTrack.setVolume(volume);
            currentTrack.play();
        } catch (Exception e) {
            Gdx.app.error("MyGames", "Error loading dungeon music: " + e.getMessage());
        }
    }


    public void playDefaultMusic() {
        if (!musicEnabled) return;

        if (isPlayingSpecialTrack) {
            stopMusic();
            isPlayingSpecialTrack = false;
        }

        if (currentTrack == null || !currentTrack.isPlaying()) {
            loadPlaylist();
            playNextTrack();
        }
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.update();
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.wav"));
        bombSound = Gdx.audio.newSound(Gdx.files.internal("laser_gun.wav"));
        knifeSound = Gdx.audio.newSound(Gdx.files.internal("Blade.wav"));
        spellCollectSound = Gdx.audio.newSound(Gdx.files.internal("Pickup_Coin.wav"));
        monsterDeathSound = Gdx.audio.newSound(Gdx.files.internal("Pickup_Magic.wav"));
        playerHurtSound = Gdx.audio.newSound(Gdx.files.internal("Pickup_Holy.wav"));
        lessHpSound = Gdx.audio.newSound(Gdx.files.internal("Shield_Pickup.wav"));
        gameOverMusic = Gdx.audio.newSound(Gdx.files.internal("GameOver.wav"));
        freezeSound = Gdx.audio.newSound(Gdx.files.internal("freeze.wav"));
        healSound = Gdx.audio.newSound(Gdx.files.internal("heal.wav"));
        twisterSound = Gdx.audio.newSound(Gdx.files.internal("twister.wav"));
        hellstormSound = Gdx.audio.newSound(Gdx.files.internal("hellstorm.wav"));
        instance = this;

        loadPreferences();
        loadPlaylist();
        playNextTrack();
        shopScreen = new ShopScreen(this);
        setScreen(new MainMenuScreen(this));
    }

    public void playFreezeSound() {
        freezeSound.play(volume);
    }

    public void playHealSound() {
        healSound.play(volume);
    }

    public void playTwisterSound() {
        twisterSound.play(volume);
    }

    public void playHellstormSound() {
        hellstormSound.play(volume);
    }
    public void playClickSound() {
        clickSound.play(volume);

    }
    public void playMonsterDeathSound() {
        monsterDeathSound.play(volume);
    }
    public void playBombSound() {
        if (bombSound != null) {
            bombSound.play(volume);
        }
    }

    public void playKnifeSound() {
        if (knifeSound != null) {
            knifeSound.play(volume);
        }
    }
    public void resetPurchases() {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        prefs.clear();
        prefs.flush();

        Preferences awardsPrefs = Gdx.app.getPreferences("AwardsPreferences");
        awardsPrefs.clear();
        awardsPrefs.flush();

        diamonds = 0;
        islandPurchased = false;
        isHousePurchased = false;
        isChinaPurchased = false;
        currentMap = "map1/map1.tmx";
        currentHero = "default";
        spellsCollected = 0;
        speedUpgradeActive = false;
        autoSpellUpgradeActive = false;
        knifeUpgradeActive = false;
        purchasedPets = null;
        purchasedHeroes = new HashSet<>();
        purchasedHeroes.add("default");
        currentPet = "none";

        // сбрасываем счетчик убитых монстров
        Preferences statsPrefs = Gdx.app.getPreferences("GameStats");
        statsPrefs.putInteger("monstersKilled", 0);
        statsPrefs.flush();
        totalMonstersKilled = 0;

        savePreferences();
    }

    public void resetAllPurchases() {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        prefs.clear();
        prefs.flush();

        Preferences awardsPrefs = Gdx.app.getPreferences("AwardsPreferences");
        awardsPrefs.clear();
        awardsPrefs.flush();

        Preferences statsPrefs = Gdx.app.getPreferences("GameStats");
        statsPrefs.putInteger("monstersKilled", 0);
        statsPrefs.flush();

        diamonds = 0;
        islandPurchased = false;
        currentMap = "map1/map1.tmx";
        currentHero = "default";
        spellsCollected = 0;
        purchasedPets = null;
        purchasedHeroes = new HashSet<>();
        purchasedHeroes.add("default");
        currentPet = "none";
        speedUpgradeActive = false;
        autoSpellUpgradeActive = false;
        knifeUpgradeActive = false;
        totalMonstersKilled = 0;

        if (mapsScreen != null) {
            mapsScreen.dispose();
            mapsScreen = new MapsScreen(this);
        }
        if (shopScreen != null) {
            shopScreen.dispose();
            shopScreen = new ShopScreen(this);
        }
        if (heroesScreen != null) {
            heroesScreen.dispose();
            heroesScreen = new HeroesScreen(this);
        }
        if (improvementsScreen != null) {
            improvementsScreen.dispose();
            improvementsScreen = new ImprovementsScreen(this);
        }

        savePreferences();
    }

    private void loadPreferences() {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        diamonds = prefs.getInteger("diamonds", 0);
        spellsCollected = prefs.getInteger("spellsCollected", 0);
        islandPurchased = prefs.getBoolean("islandPurchased", false);
        isHousePurchased = prefs.getBoolean("housePurchased", false);
        isChinaPurchased = prefs.getBoolean("chinaPurchased", false);
        currentMap = prefs.getString("currentMap", "map1/map1.tmx");
        currentHero = prefs.getString("currentHero", "default");
        speedUpgradeActive = prefs.getBoolean("speedUpgradeActive", false);
        autoSpellUpgradeActive = prefs.getBoolean("autoSpellUpgradeActive", false);
        knifeUpgradeActive = prefs.getBoolean("knifeUpgradeActive", false);
        currentPet = prefs.getString("currentPet", "none");

        totalMonstersKilled = prefs.getInteger("monstersKilled", 0);
        volume = prefs.getFloat("volume", 100.0f);
        musicEnabled = prefs.getBoolean("musicEnabled", true);
        lastLevelWasForest = prefs.getBoolean("lastLevelWasForest", false);

        currentTrackIndex = prefs.getInteger("lastTrackIndex", 0);
        tempSpeedUpgradeActive = prefs.getBoolean("tempSpeedUpgradeActive", false);
        tempAutoSpellUpgradeActive = prefs.getBoolean("tempAutoSpellUpgradeActive", false);
        tempKnifeUpgradeActive = prefs.getBoolean("tempKnifeUpgradeActive", false);
        totalMonstersKilled = getTotalMonstersKilled();
        String petString = prefs.getString("purchasedPets", "");
        purchasedPets = new HashSet<>();
        if (!petString.isEmpty()) {
            String[] pets = petString.split(",");
            for (String pet : pets) {
                purchasedPets.add(pet);
            }
        }

        String heroesString = prefs.getString("purchasedHeroes", "default");
        purchasedHeroes = new HashSet<>(Arrays.asList(heroesString.split(",")));
    }

    public void resetActiveUpgrades() {
        deactivateSpeedUpgrade();
        deactivateAutoSpellUpgrade();
        deactivateKnifeUpgrade();
        savePreferences();
    }

    public void savePreferences() {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        prefs.putInteger("diamonds", diamonds);
        prefs.putInteger("spellsCollected", spellsCollected);
        prefs.putBoolean("islandPurchased", islandPurchased);
        prefs.putBoolean("housePurchased", isHousePurchased);
        prefs.putBoolean("chinaPurchased", isChinaPurchased);
        prefs.putString("currentMap", currentMap);
        prefs.putString("currentHero", currentHero);
        prefs.putBoolean("speedUpgradeActive", speedUpgradeActive);
        prefs.putBoolean("autoSpellUpgradeActive", autoSpellUpgradeActive);
        prefs.putBoolean("knifeUpgradeActive", knifeUpgradeActive);
        prefs.putBoolean("musicEnabled", musicEnabled);
        prefs.putFloat("volume", volume);
        prefs.putBoolean("lastLevelWasForest", lastLevelWasForest);

        prefs.putInteger("lastTrackIndex", currentTrackIndex);
        prefs.putBoolean("tempSpeedUpgradeActive", tempSpeedUpgradeActive);
        prefs.putBoolean("tempAutoSpellUpgradeActive", tempAutoSpellUpgradeActive);
        prefs.putBoolean("tempKnifeUpgradeActive", tempKnifeUpgradeActive);
        prefs.putString("currentPet", currentPet);

        StringBuilder sb = new StringBuilder();
        if (purchasedPets != null) {
            for (String pet : purchasedPets) {
                sb.append(pet).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        prefs.putString("purchasedPets", sb.toString());

        String heroesString = String.join(",", purchasedHeroes);
        prefs.putString("purchasedHeroes", heroesString);

        prefs.flush();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if (currentScreen != null) {
            currentScreen.render(Gdx.graphics.getDeltaTime());
        } else {
            Gdx.app.error("MyGames", "Current screen is null!");
            setScreen(new MainMenuScreen(this));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (monsterDeathSound != null) monsterDeathSound.dispose();
        if (spellCollectSound != null) spellCollectSound.dispose();
        if (gameOverMusic != null) gameOverMusic.dispose();
        if (playerHurtSound != null) playerHurtSound.dispose();
        if (lessHpSound != null) lessHpSound.dispose();
        if (freezeSound != null) freezeSound.dispose();
        if (healSound != null) healSound.dispose();
        if (twisterSound != null) twisterSound.dispose();
        if (hellstormSound != null) hellstormSound.dispose();
        if (clickSound != null) {
            clickSound.dispose();
        }
        if (currentScreen != null) {
            currentScreen.dispose();
        }

        if (playlist != null) {
            for (Music track : playlist) {
                track.stop();
                track.dispose();
            }
        }
        if (bombSound != null) bombSound.dispose();
        if (knifeSound != null) knifeSound.dispose();

        if (shopScreen != null) {
            shopScreen.dispose();
        }
        if (heroesScreen != null) {
            heroesScreen.dispose();
        }
        if (mapsScreen != null) {
            mapsScreen.dispose();
        }
        if (improvementsScreen != null) {
            improvementsScreen.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        if (currentScreen != null) {
            currentScreen.resize(width, height);
        }
    }

    public void setScreen(Screen screen) {
        System.out.println("Setting screen: " + (screen != null ? screen.getClass().getSimpleName() : "null"));

        if (screen instanceof PauseScreen) {
            pauseCurrentTrack();
        }
        else if (currentScreen instanceof PauseScreen) {
            if (lastLevelWasForest) {
                resumeCurrentTrack(); // Для ForestScreen
            } else {
                resumeCurrentTrack(); // Для DungeonScreen
            }}
        else if (currentScreen instanceof DungeonScreen ) {
            pauseCurrentTrack();
        }
        else if (screen instanceof DungeonScreen && currentScreen instanceof PS2) {
            resumeCurrentTrack();
        } else if (currentScreen instanceof CloudScreen) {
            pauseCurrentTrack();

        } else if (screen instanceof CloudScreen && currentScreen instanceof CloudPause) {
            resumeCurrentTrack();

        }
        if (currentScreen != null) {
            currentScreen.hide();
            currentScreen.dispose();
        }

        this.currentScreen = screen;

        if (this.currentScreen != null) {
            this.currentScreen.show();
            this.currentScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }
    public SpriteBatch getBatch() {
        return batch;
    }

    public void saveDiamonds() {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        prefs.putInteger("diamonds", diamonds);
        prefs.flush();
    }

    public void loadDiamonds() {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        diamonds = prefs.getInteger("diamonds", 0);
    }

    public ShopScreen getShopScreen() {
        return shopScreen;
    }

    public void setShopScreen(ShopScreen shopScreen) {
        this.shopScreen = shopScreen;
    }

    public HeroesScreen getHeroesScreen() {
        return heroesScreen;
    }

    public void setHeroesScreen(HeroesScreen heroesScreen) {
        this.heroesScreen = heroesScreen;
    }

    public MapsScreen getMapsScreen() {
        return mapsScreen;
    }

    public void setMapsScreen(MapsScreen mapsScreen) {
        this.mapsScreen = mapsScreen;
    }

    public ImprovementsScreen getImprovementsScreen() {
        return improvementsScreen;
    }

    public void setImprovementsScreen(ImprovementsScreen improvementsScreen) {
        this.improvementsScreen = improvementsScreen;
    }
}
