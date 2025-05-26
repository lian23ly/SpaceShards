package game.info;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AwardsScreen implements Screen {
    private final MyGames game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture background;

    private Texture backBtnNormal, backBtnPressed;
    private Texture yesBtnNormal, yesBtnPressed;
    private Texture awardTexture;
    private Texture diamondTexture;
    private Texture spellTexture;
    private Texture monsterTexture;

    private ImageButton backButton;
    private ImageButton yesButton;
    private ImageButton[] awardButtons;

    private BitmapFont font;
    private Label titleLabel;
    private Label[] awardLabels;
    private Label[] rewardLabels;
    private Label monstersKilledLabel;

    private int selectedAward = -1;
    private boolean[] awardsClaimed;
    private boolean[] awardsAvailable;

    private static final int[] MONSTER_REQUIREMENTS = {50, 100, 150};
    private static final int[] DIAMOND_REWARDS = {500, 0, 0};
    private static final int[] SPELL_REWARDS = {0, 30, 50};


    private static final float CONTROL_BUTTON_SCALE = 0.7f;
    private static final float AWARD_SIZE = 200f;
    private static final float RESOURCE_SIZE = 100f;

    private static final float SELECTED_ALPHA = 1f;
    private static final float UNSELECTED_ALPHA = 0.5f;

    public AwardsScreen(MyGames game) {
        this.game = game;
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        background = new Texture(Gdx.files.internal("space1.png"));
        backBtnNormal = new Texture(Gdx.files.internal("back01.png"));
        backBtnPressed = new Texture(Gdx.files.internal("back02.png"));
        yesBtnNormal = new Texture(Gdx.files.internal("yes01.png"));
        yesBtnPressed = new Texture(Gdx.files.internal("yes02.png"));
        awardTexture = new Texture(Gdx.files.internal("award_icon.png"));
        diamondTexture = new Texture(Gdx.files.internal("stone.png"));
        spellTexture = new Texture(Gdx.files.internal("spell.png"));
        monsterTexture = new Texture(Gdx.files.internal("monster.png"));

        font = new BitmapFont();
        font.getData().setScale(3f);

        awardsClaimed = new boolean[3];
        awardsAvailable = new boolean[3];
        awardButtons = new ImageButton[3];

        Preferences prefs = Gdx.app.getPreferences("AwardsPreferences");
        for (int i = 0; i < 3; i++) {
            awardsClaimed[i] = prefs.getBoolean("awardClaimed" + i, false);
        }

        createUI();
        updateAwardsAvailability();
    }

    private void updateAwardsAvailability() {
        int totalMonstersKilled = getTotalMonstersKilled();

        for (int i = 0; i < 3; i++) {
            awardsAvailable[i] = !awardsClaimed[i] && (totalMonstersKilled >= MONSTER_REQUIREMENTS[i]);
            updateAwardLabel(i);

            float alpha = awardsAvailable[i] ?
                (selectedAward == i ? SELECTED_ALPHA : UNSELECTED_ALPHA) :
                0.5f;
            awardButtons[i].getColor().a = alpha;
        }

        monstersKilledLabel.setText("Killed: " + totalMonstersKilled);
    }

    private int getTotalMonstersKilled() {
        Preferences prefs = Gdx.app.getPreferences("GameStats");
        return prefs.getInteger("monstersKilled", 0);
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);


        titleLabel = new Label("AWARDS", new Label.LabelStyle(font, Color.WHITE));


        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();
        backStyle.up = new TextureRegionDrawable(backBtnNormal);
        backStyle.down = new TextureRegionDrawable(backBtnPressed);
        backButton = new ImageButton(backStyle);
        backButton.getImage().setScale(CONTROL_BUTTON_SCALE);
        backButton.setSize(backBtnNormal.getWidth() * CONTROL_BUTTON_SCALE, backBtnNormal.getHeight() * CONTROL_BUTTON_SCALE);

        ImageButton.ImageButtonStyle yesStyle = new ImageButton.ImageButtonStyle();
        yesStyle.up = new TextureRegionDrawable(yesBtnNormal);
        yesStyle.down = new TextureRegionDrawable(yesBtnPressed);
        yesButton = new ImageButton(yesStyle);
        yesButton.getImage().setScale(CONTROL_BUTTON_SCALE);
        yesButton.setSize(yesBtnNormal.getWidth() * CONTROL_BUTTON_SCALE, yesBtnNormal.getHeight() * CONTROL_BUTTON_SCALE);


        awardLabels = new Label[3];
        rewardLabels = new Label[3];
        Table awardsTable = new Table();
        awardsTable.padTop(30);

        for (int i = 0; i < 3; i++) {
            awardButtons[i] = createAwardButton(i);
            awardLabels[i] = new Label("", new Label.LabelStyle(font, Color.WHITE));
            rewardLabels[i] = new Label("", new Label.LabelStyle(font, Color.WHITE));

            Table awardCell = new Table();
            awardCell.add(awardButtons[i]).size(AWARD_SIZE).row();
            awardCell.add(awardLabels[i]).padTop(5).row();
            awardCell.add(rewardLabels[i]).padTop(2);

            awardsTable.add(awardCell).pad(15);
        }


        Table controlsTable = new Table();
        controlsTable.add(backButton).size(backButton.getWidth(), backButton.getHeight()).padRight(30);
        controlsTable.add(yesButton).size(yesButton.getWidth(), yesButton.getHeight());

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

        Table monstersTable = new Table();
        monstersTable.setFillParent(true);
        monstersTable.top().left();

        monstersKilledLabel = new Label("Killed: " + getTotalMonstersKilled(),
            new Label.LabelStyle(font, Color.WHITE));

        monstersTable.add(monstersKilledLabel).padLeft(65).padTop(50);

        mainTable.add(titleLabel).padTop(15).row();
        mainTable.add(awardsTable).center().row();
        mainTable.add(controlsTable).padBottom(15);

        stage.addActor(resourcesTable);
        stage.addActor(monstersTable);
        setupListeners();
    }

    private ImageButton createAwardButton(final int awardIndex) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(awardTexture);
        style.down = new TextureRegionDrawable(awardTexture);

        ImageButton button = new ImageButton(style);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();

                if (awardsAvailable[awardIndex] && !awardsClaimed[awardIndex]) {
                    for (ImageButton btn : awardButtons) {
                        btn.getColor().a = UNSELECTED_ALPHA;
                    }
                    button.getColor().a = SELECTED_ALPHA;
                    selectedAward = awardIndex;
                }
            }
        });
        return button;
    }

    private void updateAwardLabel(int index) {
        if (awardsClaimed[index]) {
            awardLabels[index].setText("CLAIMED!");
            awardLabels[index].setColor(Color.GREEN);
        } else if (awardsAvailable[index]) {
            awardLabels[index].setText("AVAILABLE!");
            awardLabels[index].setColor(Color.YELLOW);
        } else {
            awardLabels[index].setText(MONSTER_REQUIREMENTS[index] + " MONSTERS");
            awardLabels[index].setColor(Color.RED);
        }

        if (DIAMOND_REWARDS[index] > 0) {
            rewardLabels[index].setText(DIAMOND_REWARDS[index] + " DIAMONDS");
            rewardLabels[index].setColor(awardsAvailable[index] ? Color.GREEN : Color.RED);
        } else if (SPELL_REWARDS[index] > 0) {
            rewardLabels[index].setText(SPELL_REWARDS[index] + " SPELLS");
            rewardLabels[index].setColor(awardsAvailable[index] ? Color.GREEN : Color.RED);
        }
    }

    private void setupListeners() {
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                game.setScreen(new GameScreen(game));
            }
        });

        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClickSound();
                if (selectedAward != -1 && awardsAvailable[selectedAward] && !awardsClaimed[selectedAward]) {
                    claimAward(selectedAward);
                }
            }
        });
    }

    private void claimAward(int awardIndex) {
        if (DIAMOND_REWARDS[awardIndex] > 0) {
            MyGames.diamonds += DIAMOND_REWARDS[awardIndex];
        }

        if (SPELL_REWARDS[awardIndex] > 0) {
            game.spellsCollected += SPELL_REWARDS[awardIndex];
        }

        awardsClaimed[awardIndex] = true;
        Preferences prefs = Gdx.app.getPreferences("AwardsPreferences");
        prefs.putBoolean("awardClaimed" + awardIndex, true);
        prefs.flush();

        game.savePreferences();
        updateAwardsAvailability();
        selectedAward = -1;

        for (ImageButton btn : awardButtons) {
            btn.getColor().a = UNSELECTED_ALPHA;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        background.dispose();
        backBtnNormal.dispose();
        backBtnPressed.dispose();
        yesBtnNormal.dispose();
        yesBtnPressed.dispose();
        awardTexture.dispose();
        diamondTexture.dispose();
        spellTexture.dispose();
        monsterTexture.dispose();
        font.dispose();
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
