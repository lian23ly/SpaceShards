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

public class ImprovementsScreen implements Screen {
    private final MyGames game;
    private Stage stage;
    private Texture background;
    private Texture backBtnNormal, backBtnPressed;
    private Texture yesBtnNormal, yesBtnPressed;
    private Texture weaponImpTexture, speedImpTexture, autoSpellTexture;
    private Texture diamondTexture;
    private Texture spellTexture;

    private ImageButton backButton, yesButton;
    private ImageButton weaponImpButton, speedImpButton, autoSpellButton;

    private BitmapFont font;
    private Label titleLabel;
    private Label weaponImpPriceLabel, speedImpPriceLabel, autoSpellPriceLabel;

    private String selectedImprovement = "";
    private boolean canBuyWeapon = false;
    private boolean canBuySpeed = false;
    private boolean canBuyAutoSpell = false;

    private static final int WEAPON_IMP_PRICE = 15;
    private static final int SPEED_IMP_PRICE = 20;
    private static final int AUTO_SPELL_IMP_PRICE = 50;
    private static final float IMP_BUTTON_SCALE = 25f;
    private static final float CONTROL_BUTTON_SCALE = 0.7f;
    private static final float RESOURCE_SIZE = 100f;

    private SpriteBatch batch;
    private Texture backgroundTexture;

    public ImprovementsScreen(final MyGames game) {
        batch = new SpriteBatch();
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        game.playDefaultMusic();

        backgroundTexture = new Texture(Gdx.files.internal("space1.png"));
        background = new Texture(Gdx.files.internal("space1.png"));
        backBtnNormal = new Texture(Gdx.files.internal("back01.png"));
        backBtnPressed = new Texture(Gdx.files.internal("back02.png"));
        yesBtnNormal = new Texture(Gdx.files.internal("yes01.png"));
        yesBtnPressed = new Texture(Gdx.files.internal("yes02.png"));
        weaponImpTexture = new Texture(Gdx.files.internal("imp/knife.png"));
        speedImpTexture = new Texture(Gdx.files.internal("imp/speed_x2.png"));
        autoSpellTexture = new Texture(Gdx.files.internal("imp/spell_auto.png"));
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

        titleLabel = new Label("IMPROVEMENTS", new Label.LabelStyle(font, Color.WHITE));

        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();
        backStyle.up = new TextureRegionDrawable(backBtnNormal);
        backStyle.down = new TextureRegionDrawable(backBtnPressed);

        ImageButton.ImageButtonStyle yesStyle = new ImageButton.ImageButtonStyle();
        yesStyle.up = new TextureRegionDrawable(yesBtnNormal);
        yesStyle.down = new TextureRegionDrawable(yesBtnPressed);

        weaponImpButton = createImpButton(weaponImpTexture, IMP_BUTTON_SCALE);
        speedImpButton = createImpButton(speedImpTexture, IMP_BUTTON_SCALE);
        autoSpellButton = createImpButton(autoSpellTexture, IMP_BUTTON_SCALE);

        backButton = new ImageButton(backStyle);
        backButton.getImage().setScale(CONTROL_BUTTON_SCALE);
        backButton.setSize(backBtnNormal.getWidth() * CONTROL_BUTTON_SCALE,
            backBtnNormal.getHeight() * CONTROL_BUTTON_SCALE);

        yesButton = new ImageButton(yesStyle);
        yesButton.getImage().setScale(CONTROL_BUTTON_SCALE);
        yesButton.setSize(yesBtnNormal.getWidth() * CONTROL_BUTTON_SCALE,
            yesBtnNormal.getHeight() * CONTROL_BUTTON_SCALE);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle descStyle = new Label.LabelStyle(font, Color.YELLOW);

        weaponImpPriceLabel = new Label(WEAPON_IMP_PRICE + " spells", labelStyle);
        Label weaponDesc = new Label("* Double attack power", descStyle);

        speedImpPriceLabel = new Label(SPEED_IMP_PRICE + " spells", labelStyle);
        Label speedDesc = new Label("* 2x movement speed", descStyle);

        autoSpellPriceLabel = new Label(AUTO_SPELL_IMP_PRICE + " diamonds", labelStyle);
        Label autoSpellDesc = new Label("* Auto-collect spells", descStyle);

        Table improvementsTable = new Table();
        improvementsTable.padTop(30);

        Table firstRow = new Table();
        firstRow.add(createImpCell(weaponImpButton, weaponImpPriceLabel, weaponDesc)).padRight(40).padBottom(30);
        firstRow.add(createImpCell(speedImpButton, speedImpPriceLabel, speedDesc)).padBottom(30);
        improvementsTable.add(firstRow).row();

        Table secondRow = new Table();
        secondRow.add(createImpCell(autoSpellButton, autoSpellPriceLabel, autoSpellDesc)).colspan(2);
        improvementsTable.add(secondRow);

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
        mainTable.row().padTop(100);
        mainTable.add(titleLabel).padTop(60).row();
        mainTable.add(improvementsTable).center().row();
        mainTable.add(controlsTable).center().padBottom(40);

        stage.addActor(resourcesTable);
        updateButtonsState();
        setupListeners();
    }

    private ImageButton createImpButton(Texture texture, float scale) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(texture);
        style.down = new TextureRegionDrawable(texture);

        ImageButton button = new ImageButton(style);
        button.getImage().setScale(scale);
        button.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        return button;
    }

    private Table createImpCell(ImageButton button, Label priceLabel, Label description) {
        Table cell = new Table();

        Table buttonWithDesc = new Table();
        buttonWithDesc.add(button).size(button.getWidth(), button.getHeight()).padRight(20);
        buttonWithDesc.add(description).left();

        cell.add(buttonWithDesc).row();
        cell.add(priceLabel).padTop(10);

        return cell;
    }

    private void setupListeners() {
        weaponImpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                if (!game.isTempKnifeUpgradeActive()) {
                    selectedImprovement = "weapon";
                    updateButtonsState();
                }
            }
        });

        speedImpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                if (!game.isTempSpeedUpgradeActive()) {
                    selectedImprovement = "speed";
                    updateButtonsState();
                }
            }
        });

        autoSpellButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                if (!game.isTempAutoSpellUpgradeActive()) {
                    selectedImprovement = "auto_spell";
                    updateButtonsState();
                }
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
                if (selectedImprovement.equals("weapon") && game.spellsCollected >= WEAPON_IMP_PRICE && !game.isTempKnifeUpgradeActive()) {
                    game.spellsCollected -= WEAPON_IMP_PRICE;
                    game.activateTempKnifeUpgrade();
                    selectedImprovement = "";
                    updateButtonsState();

                } else if (selectedImprovement.equals("speed") && game.spellsCollected >= SPEED_IMP_PRICE && !game.isTempSpeedUpgradeActive()) {
                    game.spellsCollected -= SPEED_IMP_PRICE;
                    game.activateTempSpeedUpgrade();
                    selectedImprovement = "";
                    updateButtonsState();

                } else if (selectedImprovement.equals("auto_spell") && game.diamonds >= AUTO_SPELL_IMP_PRICE && !game.isTempAutoSpellUpgradeActive()) {
                    game.diamonds -= AUTO_SPELL_IMP_PRICE;
                    game.activateTempAutoSpellUpgrade();
                    selectedImprovement = "";
                    updateButtonsState();
                }
            }
        });
    }

    private void updateButtonsState() {
        canBuyWeapon = game.spellsCollected >= WEAPON_IMP_PRICE && !game.isTempKnifeUpgradeActive();
        canBuySpeed = game.spellsCollected >= SPEED_IMP_PRICE && !game.isTempSpeedUpgradeActive();
        canBuyAutoSpell = game.diamonds >= AUTO_SPELL_IMP_PRICE && !game.isTempAutoSpellUpgradeActive();

        float selectedAlpha = 1f;
        float unselectedAlpha = 0.7f;
        float purchasedAlpha = 0.4f;

        weaponImpButton.getColor().a = game.isTempKnifeUpgradeActive() ? purchasedAlpha :
            (selectedImprovement.equals("weapon") ? selectedAlpha : unselectedAlpha);
        speedImpButton.getColor().a = game.isTempSpeedUpgradeActive() ? purchasedAlpha :
            (selectedImprovement.equals("speed") ? selectedAlpha : unselectedAlpha);
        autoSpellButton.getColor().a = game.isTempAutoSpellUpgradeActive() ? purchasedAlpha :
            (selectedImprovement.equals("auto_spell") ? selectedAlpha : unselectedAlpha);

        weaponImpPriceLabel.setColor(game.isTempKnifeUpgradeActive() ? Color.GRAY :
            (canBuyWeapon ? Color.GREEN : Color.RED));
        speedImpPriceLabel.setColor(game.isTempSpeedUpgradeActive() ? Color.GRAY :
            (canBuySpeed ? Color.GREEN : Color.RED));
        autoSpellPriceLabel.setColor(game.isTempAutoSpellUpgradeActive() ? Color.GRAY :
            (canBuyAutoSpell ? Color.GREEN : Color.RED));

        if (game.isTempKnifeUpgradeActive()) {
            weaponImpPriceLabel.setText("PURCHASED");
        } else {
            weaponImpPriceLabel.setText(WEAPON_IMP_PRICE + " spells");
        }

        if (game.isTempSpeedUpgradeActive()) {
            speedImpPriceLabel.setText("PURCHASED");
        } else {
            speedImpPriceLabel.setText(SPEED_IMP_PRICE + " spells");
        }

        if (game.isTempAutoSpellUpgradeActive()) {
            autoSpellPriceLabel.setText("PURCHASED");
        } else {
            autoSpellPriceLabel.setText(AUTO_SPELL_IMP_PRICE + " diamonds");
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
    public void dispose() {
        if (stage != null) stage.dispose();
        if (background != null) background.dispose();
        if (backBtnNormal != null) backBtnNormal.dispose();
        if (backBtnPressed != null) backBtnPressed.dispose();
        if (yesBtnNormal != null) yesBtnNormal.dispose();
        if (yesBtnPressed != null) yesBtnPressed.dispose();
        if (weaponImpTexture != null) weaponImpTexture.dispose();
        if (speedImpTexture != null) speedImpTexture.dispose();
        if (autoSpellTexture != null) autoSpellTexture.dispose();
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
