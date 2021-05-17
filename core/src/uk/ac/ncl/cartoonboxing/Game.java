package uk.ac.ncl.cartoonboxing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Arrays;

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
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Level currentLevel;
    private PlayerCharacter playerCharacter;
    private Array<BaseCharacter> characterArray;
    private Array<HostileCharacter> botArray;
    private final int GLOBAL_MOVING_SPEED_PX = 1000;
    private long lastSpawnTime;
    private long lastGameOverTime = 0;
    private final long SPAWN_DELTA_TIME = 1000000000L;
    private Texture backgroundTexture;
    FreeTypeFontGenerator fontGenerator;
    @Getter
    private int currentScore;
    @Getter
    private int highScore;
    private Sprite backgroundSprite;
    private boolean isGameStarted;
    private boolean isGameOver;
    private GameInstance gameInstance;


    BitmapFont scoreFont;
    BitmapFont welcomeFont;
    BitmapFont gameOverFont;
    BitmapFont scoreAchieved;

    public Game(final GameInstance gameInstance) {
        this.gameInstance = gameInstance;
        isGameStarted = false;
        isGameOver = false;
        currentScore = 0;
        highScore = 0;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        batch = new SpriteBatch();
        currentLevel = new Level();
        characterArray = new Array<BaseCharacter>();
        botArray = new Array<HostileCharacter>();
        GameDimensions.update();
        playerCharacter = new PlayerCharacter();
        characterArray.add(playerCharacter);
        lastSpawnTime = TimeUtils.nanoTime();
        // Fonts
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenComicFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 100;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.color = Color.WHITE;
        fontParameter.borderWidth = 20;
        scoreFont = fontGenerator.generateFont(fontParameter);
        fontParameter.borderWidth = 8;
        fontParameter.size = GameDimensions.getLevelHeight() / 20;
        fontParameter.color = Color.RED;
        welcomeFont = fontGenerator.generateFont(fontParameter);
        fontParameter.size = GameDimensions.getLevelHeight() / 10;
        gameOverFont = fontGenerator.generateFont(fontParameter);
        fontParameter.size = GameDimensions.getLevelHeight() / 20;
        fontParameter.color = Color.YELLOW;
        scoreAchieved = fontGenerator.generateFont(fontParameter);

        // background
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/background.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        float heightWidthRatio = backgroundSprite.getHeight() / backgroundSprite.getWidth();
        backgroundSprite.setSize(GameDimensions.getLevelHeight() / heightWidthRatio, GameDimensions.getLevelHeight());
        backgroundSprite.setPosition(0, 0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        backgroundSprite.draw(batch);
        // if game has not been started, display welcome message
        if (!isGameStarted) {
            if (isGameOver) {
                gameOverFont.draw(batch, "Game over!", 0, (float)GameDimensions.getLevelHeight() * 2 / 3, GameDimensions.getLevelWidth(), Align.center, true);
                scoreAchieved.draw(batch, "Your score: " + currentScore + "\nTap anywhere to try again", 0, (float)GameDimensions.getLevelHeight() / 3, GameDimensions.getLevelWidth(), Align.center, true);
            }
            else {
                drawCharacter(playerCharacter, batch);
                welcomeFont.draw(batch, "Welcome to Cartoon Boxing!\nTap anywhere to start", 0, (float) GameDimensions.getLevelHeight() / 2, GameDimensions.getLevelWidth(), Align.center, true);
            }
        } else {
            scoreFont.draw(batch, "Score: " + currentScore + "\nHigh Score: " + highScore, 50, GameDimensions.getLevelHeight() - 50);
            for (BaseCharacter character : characterArray) {
                drawCharacter(character, batch);
            }
        }
        batch.end();

        if (isGameStarted) {
            performLevelActivities();
        } else if (Gdx.input.justTouched()) {
            isGameStarted = true;
            currentScore = 0;
            isGameOver = false;
        }
    }

    private void drawCharacter(BaseCharacter character, Batch batch) {
        boolean flip = character.isTextureNotPositionedCorrectly();
        float height = character.getRectangle().height;
        float width = character.getRectangle().width;
        batch.draw(character.getCharacterType().getTexture(), flip ? character.getX() + width : character.getX(), character.getY(), flip ? -width : width, height);
    }

    @Override
    public void dispose() {
        for (BaseCharacter character : characterArray) {
            character.getCharacterType().getTexture().dispose();
        }
        batch.dispose();
        fontGenerator.dispose();
        backgroundTexture.dispose();
    }

    private void performLevelActivities(){
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
        removeBot(character);
        currentScore++;
    }

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
        if (TimeUtils.nanoTime() - lastGameOverTime > 1000000000){
            lastGameOverTime = TimeUtils.nanoTime();
            lastSpawnTime = TimeUtils.nanoTime();
            isGameOver = true;
            isGameStarted = false;
            if (currentScore > highScore)
                highScore = currentScore;
            eradicateAllCharacters();
            playerCharacter = new PlayerCharacter();
            characterArray.add(playerCharacter);
        }
    }

    private void eradicateAllCharacters() {
        Arrays.fill(characterArray.toArray(), null);
        Arrays.fill(botArray.toArray(), null);
    }

    /**
     * Method responsible for moving all characters stored in the array, and removing those that are out of level bounds.
     * Amount of movement of each character is based on their pre-defined speed, and movingSpeedPx constant
     */
    private void moveCharacters() {
        for (BaseCharacter character : characterArray) {
            float deltaTime = Gdx.graphics.getDeltaTime();
            character.updateX(GLOBAL_MOVING_SPEED_PX, deltaTime);
        }
    }

    public void removeCharactersIfAppropriate(){
        for (BaseCharacter character : characterArray) {
            if (character.isOutOfBounds()) {
                if (!character.handleOutOfBounds()) {
                    characterArray.removeValue(character, true);
                    try {
                        botArray.removeValue((HostileCharacter)character, true);
                    } catch (Exception e){}
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

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
