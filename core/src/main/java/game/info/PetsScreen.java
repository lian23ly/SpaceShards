package game.info;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashSet;
import java.util.Set;

public class PetsScreen implements Screen {
    private final MyGames game;
    private Stage stage;
    private SpriteBatch batch;

    private Texture background;
    private Texture backgroundTexture;
    private Texture backBtnNormal, backBtnPressed;
    private Texture yesBtnNormal, yesBtnPressed;
    private Texture pet1Btn1, pet1Btn2, pet2Btn1, pet2Btn2, pet3Btn1, pet3Btn2;
    private Texture diamondTexture;
    private Texture spellTexture;

    private Animation<TextureRegion> pet1Animation;
    private Animation<TextureRegion> pet2Animation;
    private Animation<TextureRegion> pet3Animation;
    private float animationTime = 0;

    private ImageButton backButton, yesButton;
    private ImageButton pet1Button, pet2Button, pet3Button;

    private BitmapFont font;
    private Label titleLabel;
    private Label pet1PriceLabel, pet2PriceLabel, pet3PriceLabel;

    private String selectedPet = "none";
    private boolean canBuyPet1 = false;
    private boolean canBuyPet2 = false;
    private boolean canBuyPet3 = false;

    private static final int PET1_PRICE = 1500;
    private static final int PET2_PRICE = 3500;
    private static final int PET3_PRICE = 8000;
    private static final float PET_BUTTON_SCALE = 10f;
    private static final float CONTROL_BUTTON_SCALE = 0.7f;
    private static final float RESOURCE_SIZE = 100f;
    private static final float ANIMATION_SPEED = 0.2f;

    private Set<String> purchasedPets;
    private Texture noBtnNormal, noBtnPressed;
    private ImageButton noButton;

    public PetsScreen(final MyGames game) {
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

        pet1Btn1 = new Texture(Gdx.files.internal("idle01.png"));
        pet1Btn2 = new Texture(Gdx.files.internal("idle02.png"));
        pet2Btn1 = new Texture(Gdx.files.internal("goblin01.png"));
        pet2Btn2 = new Texture(Gdx.files.internal("goblin02.png"));
        pet3Btn1 = new Texture(Gdx.files.internal("zombie01.png"));
        pet3Btn2 = new Texture(Gdx.files.internal("zombie02.png"));
        noBtnNormal = new Texture(Gdx.files.internal("no01.png"));
        noBtnPressed = new Texture(Gdx.files.internal("no02.png"));

        diamondTexture = new Texture(Gdx.files.internal("stone.png"));
        spellTexture = new Texture(Gdx.files.internal("spell.png"));

        pet1Animation = createAnimation(pet1Btn1, pet1Btn2);
        pet2Animation = createAnimation(pet2Btn1, pet2Btn2);
        pet3Animation = createAnimation(pet3Btn1, pet3Btn2);

        font = new BitmapFont();
        font.getData().setScale(3f);

        purchasedPets = game.getPurchasedPets();
        if (purchasedPets == null) {
            purchasedPets = new HashSet<>();
            game.setPurchasedPets(purchasedPets);
            game.savePreferences();
        }

        createUI();
    }

    private Animation<TextureRegion> createAnimation(Texture frame1, Texture frame2) {
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(frame1));
        frames.add(new TextureRegion(frame2));
        return new Animation<>(ANIMATION_SPEED, frames, Animation.PlayMode.LOOP);
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        titleLabel = new Label("CHOOSE YOUR PET", new Label.LabelStyle(font, Color.WHITE));

        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();
        backStyle.up = new TextureRegionDrawable(backBtnNormal);
        backStyle.down = new TextureRegionDrawable(backBtnPressed);

        ImageButton.ImageButtonStyle yesStyle = new ImageButton.ImageButtonStyle();
        yesStyle.up = new TextureRegionDrawable(yesBtnNormal);
        yesStyle.down = new TextureRegionDrawable(yesBtnPressed);

        pet1Button = createPetButton(pet1Btn1, PET_BUTTON_SCALE);
        pet2Button = createPetButton(pet2Btn1, PET_BUTTON_SCALE);
        pet3Button = createPetButton(pet3Btn1, PET_BUTTON_SCALE);

        backButton = new ImageButton(backStyle);
        backButton.getImage().setScale(CONTROL_BUTTON_SCALE);
        backButton.setSize(backBtnNormal.getWidth() * CONTROL_BUTTON_SCALE,
            backBtnNormal.getHeight() * CONTROL_BUTTON_SCALE);

        yesButton = new ImageButton(yesStyle);
        yesButton.getImage().setScale(CONTROL_BUTTON_SCALE);
        yesButton.setSize(yesBtnNormal.getWidth() * CONTROL_BUTTON_SCALE,
            yesBtnNormal.getHeight() * CONTROL_BUTTON_SCALE);
        ImageButton.ImageButtonStyle noStyle = new ImageButton.ImageButtonStyle();
        noStyle.up = new TextureRegionDrawable(noBtnNormal);
        noStyle.down = new TextureRegionDrawable(noBtnPressed);

        noButton = new ImageButton(noStyle);
        noButton.getImage().setScale(CONTROL_BUTTON_SCALE);
        noButton.setSize(noBtnNormal.getWidth() * CONTROL_BUTTON_SCALE,
            noBtnNormal.getHeight() * CONTROL_BUTTON_SCALE);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        pet1PriceLabel = new Label(String.valueOf(PET1_PRICE), labelStyle);
        pet2PriceLabel = new Label(String.valueOf(PET2_PRICE), labelStyle);
        pet3PriceLabel = new Label(String.valueOf(PET3_PRICE), labelStyle);

        Table petsTable = new Table();
        petsTable.padTop(30);

        Table firstRow = new Table();
        firstRow.add(createPetCell(pet1Button, pet1PriceLabel, "pet1")).padRight(40).padBottom(30);
        firstRow.add(createPetCell(pet2Button, pet2PriceLabel, "pet2")).padRight(40).padBottom(30);
        firstRow.add(createPetCell(pet3Button, pet3PriceLabel, "pet3")).padBottom(30);
        petsTable.add(firstRow);

        Table controlsTable = new Table();
        controlsTable.padTop(30);
        controlsTable.add(backButton).size(backButton.getWidth(), backButton.getHeight()).padRight(20);
        controlsTable.add(yesButton).size(yesButton.getWidth(), yesButton.getHeight()).padRight(20);
        controlsTable.add(noButton).size(noButton.getWidth(), noButton.getHeight());

        Table resourcesTable = new Table();
        resourcesTable.setFillParent(true);
        resourcesTable.top().right();

        ImageButton spellIcon = new ImageButton(new TextureRegionDrawable(spellTexture));
        spellIcon.setSize(RESOURCE_SIZE, RESOURCE_SIZE);
        Label spellsLabel = new Label(String.valueOf(game.spellsCollected),
            new Label.LabelStyle(font, Color.WHITE));

        ImageButton diamondIcon = new ImageButton(new TextureRegionDrawable(diamondTexture));
        diamondIcon.setSize(RESOURCE_SIZE, RESOURCE_SIZE);
        Label diamondsLabel = new Label(String.valueOf(game.diamonds),
            new Label.LabelStyle(font, Color.WHITE));

        resourcesTable.add(spellsLabel).padRight(5).padTop(15);
        resourcesTable.add(spellIcon).size(RESOURCE_SIZE).padRight(15).padTop(15);
        resourcesTable.add(diamondsLabel).padRight(5).padTop(15);
        resourcesTable.add(diamondIcon).size(RESOURCE_SIZE).padRight(15).padTop(15);

        mainTable.add(titleLabel).padTop(30).row();
        mainTable.add(petsTable).center().row();
        mainTable.add(controlsTable).center().padBottom(30);

        stage.addActor(resourcesTable);
        updateButtonsState();
        setupListeners();
    }

    private ImageButton createPetButton(Texture texture, float scale) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(texture);
        style.down = new TextureRegionDrawable(texture);

        ImageButton button = new ImageButton(style);
        button.getImage().setScale(scale);
        button.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        return button;
    }

    private Table createPetCell(ImageButton button, Label priceLabel, final String petName) {
        Table cell = new Table();
        cell.add(button).size(button.getWidth(), button.getHeight()).row();
        cell.add(priceLabel).padTop(10);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                selectedPet = petName;
                updateButtonsState();
            }
        });
        return cell;
    }

    private void setupListeners() {
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new ShopScreen(game));
            }
        });
        noButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.removeCurrentPet();
                game.setScreen(new GameScreen(game));
            }
        });

        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                if (selectedPet.equals("none")) {
                    game.setCurrentPet("none");
                    game.savePreferences();
                    game.setScreen(new GameScreen(game));
                    return;
                }

                if (purchasedPets.contains(selectedPet)) {
                    game.setCurrentPet(selectedPet);
                    game.savePreferences();
                } else {
                    int price = 0;
                    switch (selectedPet) {
                        case "pet1":
                            price = PET1_PRICE;
                            break;
                        case "pet2":
                            price = PET2_PRICE;
                            break;
                        case "pet3":
                            price = PET3_PRICE;
                            break;
                    }

                    if (game.diamonds >= price) {
                        game.diamonds -= price;
                        purchasedPets.add(selectedPet);
                        game.setPurchasedPets(purchasedPets); // Update
                        game.setCurrentPet(selectedPet);
                        game.savePreferences();
                    } else {
                        return;
                    }
                }
                game.setScreen(new GameScreen(game));
            }
        });
    }

    private void updateButtonsState() {
        canBuyPet1 = game.diamonds >= PET1_PRICE && !purchasedPets.contains("pet1");
        canBuyPet2 = game.diamonds >= PET2_PRICE && !purchasedPets.contains("pet2");
        canBuyPet3 = game.diamonds >= PET3_PRICE && !purchasedPets.contains("pet3");

        float selectedAlpha = 1f;
        float unselectedAlpha = 0.7f;

        pet1Button.getColor().a = selectedPet.equals("pet1") ? selectedAlpha : unselectedAlpha;
        pet2Button.getColor().a = selectedPet.equals("pet2") ? selectedAlpha : unselectedAlpha;
        pet3Button.getColor().a = selectedPet.equals("pet3") ? selectedAlpha : unselectedAlpha;

        if (purchasedPets.contains("pet1")) {
            pet1PriceLabel.setText("OWNED");
            pet1PriceLabel.setColor(Color.GREEN);
        } else {
            pet1PriceLabel.setText(String.valueOf(PET1_PRICE));
            pet1PriceLabel.setColor(canBuyPet1 ? Color.GREEN : Color.RED);
        }

        if (purchasedPets.contains("pet2")) {
            pet2PriceLabel.setText("OWNED");
            pet2PriceLabel.setColor(Color.GREEN);
        } else {
            pet2PriceLabel.setText(String.valueOf(PET2_PRICE));
            pet2PriceLabel.setColor(canBuyPet2 ? Color.GREEN : Color.RED);
        }

        if (purchasedPets.contains("pet3")) {
            pet3PriceLabel.setText("OWNED");
            pet3PriceLabel.setColor(Color.GREEN);
        } else {
            pet3PriceLabel.setText(String.valueOf(PET3_PRICE));
            pet3PriceLabel.setColor(canBuyPet3 ? Color.GREEN : Color.RED);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        animationTime += delta;

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
        if (pet1Btn1 != null) pet1Btn1.dispose();
        if (pet1Btn2 != null) pet1Btn2.dispose();
        if (pet2Btn1 != null) pet2Btn1.dispose();
        if (pet2Btn2 != null) pet2Btn2.dispose();
        if (pet3Btn1 != null) pet3Btn1.dispose();
        if (pet3Btn2 != null) pet3Btn2.dispose();
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
