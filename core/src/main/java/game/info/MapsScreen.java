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

public class MapsScreen implements Screen {
    private final MyGames game;
    private Stage stage;
    private SpriteBatch batch;

    private Texture background;
    private Texture backgroundTexture;
    private Texture backBtnNormal, backBtnPressed;
    private Texture yesBtnNormal, yesBtnPressed;
    private Texture island1Btn, islandSpaceBtn, chinaBtn, houseBtn;
    private Texture diamondTexture, spellTexture;

    private ImageButton backButton, yesButton;
    private ImageButton island1Button, islandSpaceButton, chinaButton, houseButton;

    private BitmapFont font;
    private Label island1PriceLabel, islandSpacePriceLabel, chinaPriceLabel, housePriceLabel;

    private String selectedMap = "map1/map1.tmx";
    private boolean canBuyIsland1 = false;
    private boolean canBuyChina = false;
    private boolean canBuyHouse = false;

    private static final int ISLAND1_PRICE = 5000;
    private static final int ISLAND_SPACE_PRICE = 0;
    private static final int HOUSE_PRICE = 50;
    private Label titleLabel;
    private static final float BUTTON_SCALE = 0.7f;
    private static final float RESOURCE_SIZE = 100f;

    private boolean isDisposed = false;

    public MapsScreen(final MyGames game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        game.playDefaultMusic();
        backgroundTexture = new Texture(Gdx.files.internal("space1.png"));

        background = new Texture(Gdx.files.internal("space1.png"));
        backBtnNormal = new Texture(Gdx.files.internal("back01.png"));
        backBtnPressed = new Texture(Gdx.files.internal("back02.png"));
        yesBtnNormal = new Texture(Gdx.files.internal("yes01.png"));
        yesBtnPressed = new Texture(Gdx.files.internal("yes02.png"));
        island1Btn = new Texture(Gdx.files.internal("iland1.jpg"));
        islandSpaceBtn = new Texture(Gdx.files.internal("ilandspace.jpg"));
        chinaBtn = new Texture(Gdx.files.internal("chinamap.jpg"));
        houseBtn = new Texture(Gdx.files.internal("house.jpg"));
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
        titleLabel = new Label("CHOOSE YOUR MAP", new Label.LabelStyle(font, Color.WHITE));

        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();
        backStyle.up = new TextureRegionDrawable(backBtnNormal);
        backStyle.down = new TextureRegionDrawable(backBtnPressed);

        ImageButton.ImageButtonStyle yesStyle = new ImageButton.ImageButtonStyle();
        yesStyle.up = new TextureRegionDrawable(yesBtnNormal);
        yesStyle.down = new TextureRegionDrawable(yesBtnPressed);

        ImageButton.ImageButtonStyle island1Style = new ImageButton.ImageButtonStyle();
        island1Style.up = new TextureRegionDrawable(island1Btn);
        island1Style.down = new TextureRegionDrawable(island1Btn);

        ImageButton.ImageButtonStyle islandSpaceStyle = new ImageButton.ImageButtonStyle();
        islandSpaceStyle.up = new TextureRegionDrawable(islandSpaceBtn);
        islandSpaceStyle.down = new TextureRegionDrawable(islandSpaceBtn);

        ImageButton.ImageButtonStyle chinaStyle = new ImageButton.ImageButtonStyle();
        chinaStyle.up = new TextureRegionDrawable(chinaBtn);
        chinaStyle.down = new TextureRegionDrawable(chinaBtn);

        ImageButton.ImageButtonStyle houseStyle = new ImageButton.ImageButtonStyle();
        houseStyle.up = new TextureRegionDrawable(houseBtn);
        houseStyle.down = new TextureRegionDrawable(houseBtn);

        island1Button = new ImageButton(island1Style);
        islandSpaceButton = new ImageButton(islandSpaceStyle);
        chinaButton = new ImageButton(chinaStyle);
        houseButton = new ImageButton(houseStyle);
        backButton = new ImageButton(backStyle);
        yesButton = new ImageButton(yesStyle);

        float islandWidth = island1Btn.getWidth() * BUTTON_SCALE;
        float islandHeight = island1Btn.getHeight() * BUTTON_SCALE;

        island1Button.setSize(islandWidth, islandHeight);
        islandSpaceButton.setSize(islandWidth, islandHeight);
        chinaButton.setSize(islandWidth, islandHeight);
        houseButton.setSize(islandWidth, islandHeight);

        float controlButtonWidth = backBtnNormal.getWidth() * BUTTON_SCALE;
        float controlButtonHeight = backBtnNormal.getHeight() * BUTTON_SCALE;
        backButton.setSize(controlButtonWidth, controlButtonHeight);
        yesButton.setSize(controlButtonWidth, controlButtonHeight);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        islandSpacePriceLabel = new Label(String.valueOf(ISLAND_SPACE_PRICE), labelStyle);
        island1PriceLabel = new Label(String.valueOf(ISLAND1_PRICE), labelStyle);
        chinaPriceLabel = new Label(String.valueOf(MyGames.CHINA_MAP_SPELL_PRICE), labelStyle);
        housePriceLabel = new Label(String.valueOf(HOUSE_PRICE), labelStyle);

        Table islandSpaceTable = createMapTable(islandSpaceButton, islandSpacePriceLabel, diamondTexture);
        Table island1Table = createMapTable(island1Button, island1PriceLabel, diamondTexture);
        Table houseTable = createMapTable(houseButton, housePriceLabel, spellTexture);
        Table chinaTable = createMapTable(chinaButton, chinaPriceLabel, spellTexture);

        Table mapsTable = new Table();
        mapsTable.padTop(100);
        mapsTable.add(islandSpaceTable).padRight(50);
        mapsTable.add(island1Table).padRight(50);
        mapsTable.add(houseTable).padRight(50);
        mapsTable.add(chinaTable);

        Table controlsTable = new Table();
        controlsTable.add(backButton).size(controlButtonWidth, controlButtonHeight).padRight(50);
        controlsTable.add(yesButton).size(controlButtonWidth, controlButtonHeight);

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
        mainTable.add(mapsTable).center().row();
        mainTable.add(controlsTable).center().padTop(40);

        stage.addActor(resourcesTable);
        updateButtonsState();
        setupListeners();
    }

    private Table createMapTable(ImageButton mapButton, Label priceLabel, Texture currencyTexture) {
        Table table = new Table();
        table.add(mapButton).size(mapButton.getWidth(), mapButton.getHeight()).row();

        Table priceTable = new Table();
        priceTable.add(priceLabel).padRight(10);
        ImageButton currencyIcon = new ImageButton(new TextureRegionDrawable(currencyTexture));
        currencyIcon.setSize(30, 30);
        priceTable.add(currencyIcon).size(40);

        table.add(priceTable).padTop(10);
        return table;
    }

    private void setupListeners() {
        islandSpaceButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                selectedMap = "map1/map1.tmx";
                updateButtonsState();
            }
        });

        island1Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                selectedMap = "map4/iland.tmx";
                updateButtonsState();
            }
        });

        houseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                selectedMap = "map6/house.tmx";
                updateButtonsState();
            }
        });

        chinaButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                selectedMap = "map5/china.tmx";
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

                if (selectedMap.equals("map4/iland.tmx")) {
                    if (!game.isIslandPurchased()) {
                        if (game.diamonds >= ISLAND1_PRICE) {
                            game.diamonds -= ISLAND1_PRICE;
                            game.setIslandPurchased(true);
                        } else {
                            return;
                        }
                    }
                }
                else if (selectedMap.equals("map6/house.tmx")) {
                    if (!game.isHousePurchased()) {
                        if (game.spellsCollected >= HOUSE_PRICE) {
                            game.spellsCollected -= HOUSE_PRICE;
                            game.setHousePurchased(true);
                        } else {
                            return;
                        }
                    }
                }
                else if (selectedMap.equals("map5/china.tmx")) {
                    if (!game.isChinaPurchased()) {
                        if (game.spellsCollected >= MyGames.CHINA_MAP_SPELL_PRICE) {
                            game.spellsCollected -= MyGames.CHINA_MAP_SPELL_PRICE;
                            game.setChinaPurchased(true);
                        } else {
                            return;
                        }
                    }
                }

                game.setCurrentMap(selectedMap);
                game.savePreferences();
                game.setScreen(new GameScreen(game));
            }
        });
    }

    private void updateButtonsState() {
        canBuyIsland1 = game.diamonds >= ISLAND1_PRICE || game.isIslandPurchased();
        canBuyHouse = game.spellsCollected >= HOUSE_PRICE || game.isHousePurchased();
        canBuyChina = game.spellsCollected >= MyGames.CHINA_MAP_SPELL_PRICE || game.isChinaPurchased();

        islandSpaceButton.getColor().a = selectedMap.equals("map1/map1.tmx") ? 1f : 0.5f;
        island1Button.getColor().a = selectedMap.equals("map4/iland.tmx") ? 1f : 0.5f;
        houseButton.getColor().a = selectedMap.equals("map6/house.tmx") ? 1f : 0.5f;
        chinaButton.getColor().a = selectedMap.equals("map5/china.tmx") ? 1f : 0.5f;

        boolean enabled = selectedMap.equals("map1/map1.tmx") ||
            (selectedMap.equals("map4/iland.tmx") && canBuyIsland1) ||
            (selectedMap.equals("map6/house.tmx") && canBuyHouse) ||
            (selectedMap.equals("map5/china.tmx") && canBuyChina);
        yesButton.setDisabled(!enabled);

        if (game.isIslandPurchased()) {
            island1PriceLabel.setText("Purchased");
            island1PriceLabel.setColor(Color.GREEN);
        } else {
            island1PriceLabel.setText(String.valueOf(ISLAND1_PRICE));
            island1PriceLabel.setColor(canBuyIsland1 ? Color.WHITE : Color.RED);
        }

        if (game.isHousePurchased()) {
            housePriceLabel.setText("Purchased");
            housePriceLabel.setColor(Color.GREEN);
        } else {
            housePriceLabel.setText(String.valueOf(HOUSE_PRICE));
            housePriceLabel.setColor(canBuyHouse ? Color.WHITE : Color.RED);
        }

        if (game.isChinaPurchased()) {
            chinaPriceLabel.setText("Purchased");
            chinaPriceLabel.setColor(Color.GREEN);
        } else {
            chinaPriceLabel.setText(String.valueOf(MyGames.CHINA_MAP_SPELL_PRICE));
            chinaPriceLabel.setColor(canBuyChina ? Color.WHITE : Color.RED);
        }
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
    public void dispose() {
        if (isDisposed) return;
        isDisposed = true;
        if (stage != null) stage.dispose();
        if (background != null) background.dispose();
        if (backBtnNormal != null) backBtnNormal.dispose();
        if (backBtnPressed != null) backBtnPressed.dispose();
        if (batch != null) batch.dispose();
        if (yesBtnNormal != null) yesBtnNormal.dispose();
        if (yesBtnPressed != null) yesBtnPressed.dispose();
        if (island1Btn != null) island1Btn.dispose();
        if (islandSpaceBtn != null) islandSpaceBtn.dispose();
        if (chinaBtn != null) chinaBtn.dispose();
        if (houseBtn != null) houseBtn.dispose();
        if (diamondTexture != null) diamondTexture.dispose();
        if (spellTexture != null) spellTexture.dispose();
        if (font != null) font.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
}
