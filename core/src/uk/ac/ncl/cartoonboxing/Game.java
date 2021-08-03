package uk.ac.ncl.cartoonboxing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import lombok.Getter;
import uk.ac.ncl.cartoonboxing.character.BaseCharacter;
import uk.ac.ncl.cartoonboxing.character.HostileCharacter;
import uk.ac.ncl.cartoonboxing.character.PlayerCharacter;

/**
 * Main class of the game that controls rendering, management of game entities (characters, levels, etc.)
 *
 * @author Piotr Grela
 */
public class Game implements Screen {
    //region Constants
    private final int GLOBAL_MOVING_SPEED_PX = 1000;
    private final long SPAWN_DELTA_TIME = 1000000000L;
    //endregion
    //region Fonts
    FreeTypeFontGenerator fontGenerator;
    BitmapFont scoreFont;
    BitmapFont welcomeFont;
    BitmapFont gameOverFont;
    BitmapFont scoreAchievedFont;
    BitmapFont gamePausedFont;
    BitmapFont buttonFont;
    //endregion
    //region Misc. references
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private TextButton pauseButton;
    private Sprite backgroundSprite;
    private GameInstance gameInstance;
    private Array<Sound> soundArray;
    private Sound booSound;
    //endregion
    //region Characters and level
    private Level currentLevel;
    private PlayerCharacter playerCharacter;
    private Array<BaseCharacter> characterArray;
    private Array<HostileCharacter> botArray;
    //endregion
    //region Game-state variables
    private long lastSpawnTime;
    private long lastGameOverTime = 0;
    @Getter
    private int currentScore;
    @Getter
    private int highScore;
    //endregion
    //region Booleans
    private boolean isGamePaused;
    private boolean isGameOver;
    private boolean isNewSession;

    //endregion
    public Game(final GameInstance gameInstance) {
        this.gameInstance = gameInstance;
        //region initial game-state values
        isGamePaused = true;
        isGameOver = false;
        isNewSession = true;
        currentScore = 0;
        highScore = 0;
        lastSpawnTime = TimeUtils.nanoTime();
        //endregion
        //region Camera and batch
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        batch = gameInstance.batch;
        //endregion
        //region Level and characters
        currentLevel = new Level();
        characterArray = new Array<BaseCharacter>();
        botArray = new Array<HostileCharacter>();
        //endregion
        //region Update game dimensions based on now-established data
        GameDimensions.update();
        playerCharacter = new PlayerCharacter();
        characterArray.add(playerCharacter);
        //endregion
        //region Fonts
        //region fontGenerator and fontParameter
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenComicFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        //endregion
        //region scoreFont
        fontParameter.size = 80;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.color = Color.WHITE;
        fontParameter.borderWidth = 20;
        scoreFont = fontGenerator.generateFont(fontParameter);
        //endregion
        //region welcomeFont
        fontParameter.borderWidth = 8;
        fontParameter.size = GameDimensions.getLevelHeight() / 20;
        fontParameter.color = Color.RED;
        welcomeFont = fontGenerator.generateFont(fontParameter);
        //endregion
        //region gameOverFont
        fontParameter.size = GameDimensions.getLevelHeight() / 10;
        gameOverFont = fontGenerator.generateFont(fontParameter);
        //endregion
        //region scoreAchievedFont

        fontParameter.size = GameDimensions.getLevelHeight() / 20;
        fontParameter.color = Color.YELLOW;
        scoreAchievedFont = fontGenerator.generateFont(fontParameter);
        //endregion
        //region gamePausedFont
        fontParameter.color = Color.BLUE;
        gamePausedFont = fontGenerator.generateFont(fontParameter);
        //endregion
        //region buttonFont
        fontParameter.color = Color.WHITE;
        fontParameter.size = GameDimensions.getLevelHeight() / 30;
        buttonFont = fontGenerator.generateFont(fontParameter);
        //endregion
        //endregion
        //region Textures and buttons
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/background.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        float heightWidthRatio = backgroundSprite.getHeight() / backgroundSprite.getWidth();
        backgroundSprite.setSize(GameDimensions.getLevelHeight() / heightWidthRatio, GameDimensions.getLevelHeight());
        backgroundSprite.setPosition(0, 0);
        TextButton.TextButtonStyle pauseButtonStyle = new TextButton.TextButtonStyle();
        pauseButtonStyle.font = buttonFont;
        pauseButton = new TextButton("PAUSE", pauseButtonStyle);
        pauseButton.setPosition(GameDimensions.getLevelWidth() - 400, GameDimensions.getLevelHeight() - 100);
        pauseButton.setTouchable(Touchable.enabled);
        pauseButton.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                pause();
                return true;
            }
        });
        //endregion
        //region Sounds
        soundArray = new Array<>();
        soundArray.add(Gdx.audio.newSound(Gdx.files.internal("sounds/punch1.wav")));
        soundArray.add(Gdx.audio.newSound(Gdx.files.internal("sounds/punch2.wav")));
        soundArray.add(Gdx.audio.newSound(Gdx.files.internal("sounds/punch3.wav")));
        booSound = Gdx.audio.newSound(Gdx.files.internal("sounds/boo.wav"));
        //endregion
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        backgroundSprite.draw(batch);
        if (isGamePaused) {
            // if the game has not started, but previous round also already happened, display game over message
            if (isGameOver) {
                gameOverFont.draw(batch, "Game over!", 0, (float) GameDimensions.getLevelHeight() * 2 / 3, GameDimensions.getLevelWidth(), Align.center, true);
                scoreAchievedFont.draw(batch, "Your score: " + currentScore + "\nTap anywhere to try again", 0, (float) GameDimensions.getLevelHeight() / 3, GameDimensions.getLevelWidth(), Align.center, true);
            }
            // if game has not been started, display welcome message
            else {
                if (isNewSession) {
                    drawCharacter(playerCharacter, batch);
                    welcomeFont.draw(batch, "Welcome to Cartoon Boxing!\nTap anywhere to start", 0, (float) GameDimensions.getLevelHeight() / 2, GameDimensions.getLevelWidth(), Align.center, true);
                } else {
                    drawCharacters();
                    gamePausedFont.draw(batch, "Game paused\nTap anywhere to resume", 0, (float) GameDimensions.getLevelHeight() / 2, GameDimensions.getLevelWidth(), Align.center, true);
                    scoreFont.draw(batch, "Score: " + currentScore + "\nHigh Score: " + highScore, 50, GameDimensions.getLevelHeight() - 50);
                }
            }
        } else {
            scoreFont.draw(batch, "Score: " + currentScore + "\nHigh Score: " + highScore, 50, GameDimensions.getLevelHeight() - 50);
            drawCharacters();
            pauseButton.draw(batch, 1);
        }
        batch.end();

        if (!isGamePaused) {
            performLevelActivities();
        } else if (Gdx.input.justTouched() && TimeUtils.nanoTime() - lastGameOverTime > 1000000000) {
            booSound.stop();
            if (isGameOver) {
                isGameOver = false;
                currentScore = 0;
            }
            isGamePaused = false;
            isNewSession = false;
        }
    }

    private void drawCharacter(BaseCharacter character, Batch batch) {
        boolean flip = character.isTextureNotPositionedCorrectly();
        float height = character.getRectangle().height;
        float width = character.getRectangle().width;
        batch.draw(character.getCharacterType().getTexture(), flip ? character.getX() + width : character.getX(), character.getY(), flip ? -width : width, height);
    }

    private void drawCharacters() {
        for (BaseCharacter character : characterArray) {
            drawCharacter(character, batch);
        }
    }

    @Override
    public void dispose() {
        for (BaseCharacter character : characterArray) {
            character.getCharacterType().getTexture().dispose();
        }
        for (Sound sound : soundArray) {
            sound.dispose();
        }
        booSound.dispose();
        batch.dispose();
        fontGenerator.dispose();
        backgroundTexture.dispose();
    }

    /**
     * Perform all common checks and activities if game once game is running
     */
    private void performLevelActivities() {
        spawnBotIfAppropriate();
        checkForHit();
        checkForClick();
        removeCharactersIfAppropriate();
        moveCharacters();
    }

    private void checkForClick() {
        if (Gdx.input.justTouched()) {
            playerCharacter.flipCharacter();
        }
    }

    private void processPlayerHit(HostileCharacter character) {
        Sound punchSound = soundArray.random();
        punchSound.play();
        removeBot(character);
        // TODO Bot losing animation
        currentScore++;
    }

    /**
     * Check if player character's model overlaps with any other character's model.
     * If so, it will check who's "hit" who, and act accordingly.
     */
    private void checkForHit() {
        for (HostileCharacter character : botArray) {
            if (character.getRectangle().overlaps(playerCharacter.getRectangle())) {
                if (character.inFrontOf(playerCharacter)) {
                    processPlayerHit(character);
                } else {
                    gameOver();
                }
            }
        }
    }

    private void gameOver() {
        if (TimeUtils.nanoTime() - lastGameOverTime > 1000000000) {
            booSound.play();
            lastGameOverTime = TimeUtils.nanoTime();
            lastSpawnTime = TimeUtils.nanoTime();
            isGameOver = true;
            isGamePaused = true;
            if (currentScore > highScore)
                highScore = currentScore;
            eradicateAllCharacters();
            // TODO apply animations
            playerCharacter = new PlayerCharacter();
            characterArray.add(playerCharacter);
        }
    }

    private void eradicateAllCharacters() {
        characterArray.clear();
        botArray.clear();
//        Arrays.fill(characterArray.toArray(), null);
//        Arrays.fill(botArray.toArray(), null);
    }

    /**
     * Method responsible for moving all characters stored in the array, and removing those that are out of level bounds.
     * Amount of movement of each character is based on their pre-defined speed, and movingSpeedPx constant
     */
    private void moveCharacters() {
        for (BaseCharacter character : characterArray) {
            if (!character.isAtBoundary()) {
                float deltaTime = Gdx.graphics.getDeltaTime();
                character.updateX(GLOBAL_MOVING_SPEED_PX, deltaTime);
            }
        }
    }

    public void removeCharactersIfAppropriate() {
        for (BaseCharacter character : characterArray) {
            if (character.isOutOfBounds()) {
                if (!character.handleOutOfBounds()) {
                    characterArray.removeValue(character, true);
                    try {
                        botArray.removeValue((HostileCharacter) character, true);
                    } catch (Exception e) {
                    }
                    character = null;
                }
            }
        }
    }

    private void spawnNewBot() {
        HostileCharacter character = HostileCharacter.generateRandomCharacter(currentScore);
        botArray.add(character);
        characterArray.add(character);
        lastSpawnTime = TimeUtils.nanoTime();
    }

    private void spawnBotIfAppropriate() {
        if (TimeUtils.nanoTime() - lastSpawnTime > SPAWN_DELTA_TIME - ((currentScore + 1) * 2000)) {
            spawnNewBot();
        }
    }

    private void removeBot(HostileCharacter character) {
        botArray.removeValue(character, true);
        characterArray.removeValue(character, true);
        character = null;
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        isGamePaused = true;
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
