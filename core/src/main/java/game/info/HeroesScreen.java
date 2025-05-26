package game.info;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HeroesScreen implements Screen {
    private final MyGames game;
    private Stage stage;
    private SpriteBatch batch;

    private Texture background;
    private Texture backgroundTexture;
    private Texture backBtnNormal, backBtnPressed;
    private Texture yesBtnNormal, yesBtnPressed;
    private Texture defaultHeroBtn, manHeroBtn, dragonHeroBtn, girlHeroBtn;
    private Texture diamondTexture;
    private Texture spellTexture;

    private ImageButton backButton, yesButton;
    private ImageButton defaultHeroButton, manHeroButton, dragonHeroButton, girlHeroButton;

    private BitmapFont font;
    private Label titleLabel;
    private Label defaultHeroPriceLabel, manHeroPriceLabel, dragonHeroPriceLabel, girlHeroPriceLabel;

    private String selectedHero = "default";
    private boolean canBuyMan = false;
    private boolean canBuyDragon = false;
    private boolean canBuyGirl = false;

    private static final int DEFAULT_HERO_PRICE = 0;
    private static final int MAN_HERO_PRICE = 2000;
    private static final int DRAGON_HERO_PRICE = 6000;
    private static final int GIRL_HERO_PRICE = 10000;
    private static final float HERO_BUTTON_SCALE = 10f;
    private static final float CONTROL_BUTTON_SCALE = 0.7f;
    private static final float RESOURCE_SIZE = 100f;

    public HeroesScreen(final MyGames game) {
        this.game = game;
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        game.playDefaultMusic();

        background = new Texture(Gdx.files.internal("space1.png"));
        backgroundTexture = new Texture(Gdx.files.internal("space1.png"));
        backBtnNormal = new Texture(Gdx.files.internal("back01.png"));
        backBtnPressed = new Texture(Gdx.files.internal("back02.png"));
        yesBtnNormal = new Texture(Gdx.files.internal("yes01.png"));
        yesBtnPressed = new Texture(Gdx.files.internal("yes02.png"));

        defaultHeroBtn = new Texture(Gdx.files.internal("player.png"));
        manHeroBtn = new Texture(Gdx.files.internal("man.png"));
        dragonHeroBtn = new Texture(Gdx.files.internal("dragon.png"));
        girlHeroBtn = new Texture(Gdx.files.internal("girl.png"));
        diamondTexture = new Texture(Gdx.files.internal("stone.png"));
        spellTexture = new Texture(Gdx.files.internal("spell.png"));

        font = new BitmapFont();
        font.getData().setScale(3f);

        createUI();
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        titleLabel = new Label("CHOOSE YOUR HERO", new Label.LabelStyle(font, Color.WHITE));

        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();
        backStyle.up = new TextureRegionDrawable(backBtnNormal);
        backStyle.down = new TextureRegionDrawable(backBtnPressed);

        ImageButton.ImageButtonStyle yesStyle = new ImageButton.ImageButtonStyle();
        yesStyle.up = new TextureRegionDrawable(yesBtnNormal);
        yesStyle.down = new TextureRegionDrawable(yesBtnPressed);

        defaultHeroButton = createHeroButton(defaultHeroBtn, HERO_BUTTON_SCALE);
        manHeroButton = createHeroButton(manHeroBtn, HERO_BUTTON_SCALE);
        dragonHeroButton = createHeroButton(dragonHeroBtn, HERO_BUTTON_SCALE);
        girlHeroButton = createHeroButton(girlHeroBtn, HERO_BUTTON_SCALE);

        backButton = new ImageButton(backStyle);
        backButton.getImage().setScale(CONTROL_BUTTON_SCALE);
        backButton.setSize(backBtnNormal.getWidth() * CONTROL_BUTTON_SCALE,
            backBtnNormal.getHeight() * CONTROL_BUTTON_SCALE);

        yesButton = new ImageButton(yesStyle);
        yesButton.getImage().setScale(CONTROL_BUTTON_SCALE);
        yesButton.setSize(yesBtnNormal.getWidth() * CONTROL_BUTTON_SCALE,
            yesBtnNormal.getHeight() * CONTROL_BUTTON_SCALE);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        defaultHeroPriceLabel = new Label("FREE", labelStyle);
        manHeroPriceLabel = new Label(game.getPurchasedHeroes().contains("man") ? "OWNED" : String.valueOf(MAN_HERO_PRICE), labelStyle);
        dragonHeroPriceLabel = new Label(game.getPurchasedHeroes().contains("dragon") ? "OWNED" : String.valueOf(DRAGON_HERO_PRICE), labelStyle);
        girlHeroPriceLabel = new Label(game.getPurchasedHeroes().contains("girl") ? "OWNED" : String.valueOf(GIRL_HERO_PRICE), labelStyle);

        Table heroesTable = new Table();
        heroesTable.padTop(30);

        heroesTable.add(createHeroCell(defaultHeroButton, defaultHeroPriceLabel)).padRight(40);
        heroesTable.add(createHeroCell(manHeroButton, manHeroPriceLabel)).padRight(40);
        heroesTable.add(createHeroCell(dragonHeroButton, dragonHeroPriceLabel)).padRight(40);
        heroesTable.add(createHeroCell(girlHeroButton, girlHeroPriceLabel));

        Table controlsTable = new Table();
        controlsTable.padTop(30);
        controlsTable.add(backButton).size(backButton.getWidth(), backButton.getHeight()).padRight(40);
        controlsTable.add(yesButton).size(yesButton.getWidth(), yesButton.getHeight());

        Table resourcesTable = new Table();
        resourcesTable.setFillParent(true);
        resourcesTable.top().right();

        String spellsText = String.valueOf(game.spellsCollected);
        String diamondsText = String.valueOf(game.diamonds);

        GlyphLayout spellsLayout = new GlyphLayout(font, spellsText);
        GlyphLayout diamondsLayout = new GlyphLayout(font, diamondsText);

        float PADDING_RIGHT = 15f;
        float PADDING_TOP = 15f;
        float TEXT_ICON_SPACING = 5f;

        float currentX = Gdx.graphics.getWidth() - PADDING_RIGHT;
        float yPos = Gdx.graphics.getHeight() - PADDING_TOP - RESOURCE_SIZE;

        ImageButton spellIcon = new ImageButton(new TextureRegionDrawable(spellTexture));
        spellIcon.setSize(RESOURCE_SIZE, RESOURCE_SIZE);
        float spellsIconX = currentX - RESOURCE_SIZE;
        float spellsTextX = spellsIconX - TEXT_ICON_SPACING - spellsLayout.width;
        resourcesTable.add(new Label(spellsText, new Label.LabelStyle(font, Color.WHITE)))
            .padRight(TEXT_ICON_SPACING).padTop(PADDING_TOP);
        resourcesTable.add(spellIcon).size(RESOURCE_SIZE).padRight(PADDING_RIGHT).padTop(PADDING_TOP);

        ImageButton diamondIcon = new ImageButton(new TextureRegionDrawable(diamondTexture));
        diamondIcon.setSize(RESOURCE_SIZE, RESOURCE_SIZE);
        currentX = spellsTextX - PADDING_RIGHT;
        float diamondsIconX = currentX - RESOURCE_SIZE;
        float diamondsTextX = diamondsIconX - TEXT_ICON_SPACING - diamondsLayout.width;
        resourcesTable.add(new Label(diamondsText, new Label.LabelStyle(font, Color.WHITE)))
            .padRight(TEXT_ICON_SPACING).padTop(PADDING_TOP);
        resourcesTable.add(diamondIcon).size(RESOURCE_SIZE).padRight(PADDING_RIGHT).padTop(PADDING_TOP);

        mainTable.row().padTop(10);
        mainTable.add(titleLabel).padTop(60).row();
        mainTable.add(heroesTable).center().row();
        mainTable.add(controlsTable).center().padBottom(30);

        stage.addActor(resourcesTable);
        updateButtonsState();
        setupListeners();
    }

    private ImageButton createHeroButton(Texture texture, float scale) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(texture);
        style.down = new TextureRegionDrawable(texture);

        ImageButton button = new ImageButton(style);
        button.getImage().setScale(scale);
        button.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        return button;
    }

    private Table createHeroCell(ImageButton button, Label priceLabel) {
        Table cell = new Table();
        cell.add(button).size(button.getWidth(), button.getHeight()).row();
        cell.add(priceLabel).padTop(10);
        return cell;
    }

    private void setupListeners() {
        defaultHeroButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                selectedHero = "default";
                updateButtonsState();
            }
        });

        manHeroButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                selectedHero = "man";
                updateButtonsState();
            }
        });

        dragonHeroButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                selectedHero = "dragon";
                updateButtonsState();
            }
        });

        girlHeroButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                selectedHero = "girl";
                updateButtonsState();
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new ShopScreen(game));
            }
        });

        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                switch (selectedHero) {
                    case "man":
                        if (game.getPurchasedHeroes().contains("man") || game.diamonds >= MAN_HERO_PRICE) {
                            if (!game.getPurchasedHeroes().contains("man")) {
                                game.diamonds -= MAN_HERO_PRICE;
                                game.addPurchasedHero("man");
                            }
                            game.setCurrentHero("man");
                            game.savePreferences();
                        } else {
                            return;
                        }
                        break;
                    case "dragon":
                        if (game.getPurchasedHeroes().contains("dragon") || game.diamonds >= DRAGON_HERO_PRICE) {
                            if (!game.getPurchasedHeroes().contains("dragon")) {
                                game.diamonds -= DRAGON_HERO_PRICE;
                                game.addPurchasedHero("dragon");
                            }
                            game.setCurrentHero("dragon");
                            game.savePreferences();
                        } else {
                            return;
                        }
                        break;
                    case "girl":
                        if (game.getPurchasedHeroes().contains("girl") || game.diamonds >= GIRL_HERO_PRICE) {
                            if (!game.getPurchasedHeroes().contains("girl")) {
                                game.diamonds -= GIRL_HERO_PRICE;
                                game.addPurchasedHero("girl");
                            }
                            game.setCurrentHero("girl");
                            game.savePreferences();
                        } else {
                            return;
                        }
                        break;
                    default:
                        game.setCurrentHero("default");
                }
                game.setScreen(new GameScreen(game));
            }
        });
    }

    private void updateButtonsState() {
        canBuyMan = game.diamonds >= MAN_HERO_PRICE || game.getPurchasedHeroes().contains("man");
        canBuyDragon = game.diamonds >= DRAGON_HERO_PRICE || game.getPurchasedHeroes().contains("dragon");
        canBuyGirl = game.diamonds >= GIRL_HERO_PRICE || game.getPurchasedHeroes().contains("girl");

        float selectedAlpha = 1f;
        float unselectedAlpha = 0.7f;

        defaultHeroButton.getColor().a = selectedHero.equals("default") ? selectedAlpha : unselectedAlpha;
        manHeroButton.getColor().a = selectedHero.equals("man") ? selectedAlpha : unselectedAlpha;
        dragonHeroButton.getColor().a = selectedHero.equals("dragon") ? selectedAlpha : unselectedAlpha;
        girlHeroButton.getColor().a = selectedHero.equals("girl") ? selectedAlpha : unselectedAlpha;

        manHeroPriceLabel.setColor(game.getPurchasedHeroes().contains("man") ? Color.GREEN :
            (canBuyMan ? Color.GREEN : Color.RED));
        dragonHeroPriceLabel.setColor(game.getPurchasedHeroes().contains("dragon") ? Color.GREEN :
            (canBuyDragon ? Color.GREEN : Color.RED));
        girlHeroPriceLabel.setColor(game.getPurchasedHeroes().contains("girl") ? Color.GREEN :
            (canBuyGirl ? Color.GREEN : Color.RED));

        defaultHeroPriceLabel.setColor(Color.WHITE);
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
    public void dispose() {
        if (stage != null) stage.dispose();
        if (background != null) background.dispose();
        if (backBtnNormal != null) backBtnNormal.dispose();
        if (backBtnPressed != null) backBtnPressed.dispose();
        if (yesBtnNormal != null) yesBtnNormal.dispose();
        if (yesBtnPressed != null) yesBtnPressed.dispose();
        if (defaultHeroBtn != null) defaultHeroBtn.dispose();
        if (manHeroBtn != null) manHeroBtn.dispose();
        if (dragonHeroBtn != null) dragonHeroBtn.dispose();
        if (girlHeroBtn != null) girlHeroBtn.dispose();
        if (diamondTexture != null) diamondTexture.dispose();
        if (spellTexture != null) spellTexture.dispose();
        if (font != null) font.dispose();
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
    public void hide() {}
}
