package uk.ac.ncl.cartoonboxing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
public class Game extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Level currentLevel;
    private PlayerCharacter playerCharacter;
    private Array<BaseCharacter> characterArray;
    private Array<HostileCharacter> botArray;
    private final int GLOBAL_MOVING_SPEED_PX = 1000;
    private long lastSpawnTime;
    private final long SPAWN_DELTA_TIME = 1000000000L;
    private BitmapFont bitmapFont;
    @Getter
    private int currentScore;
    @Getter
    private int highScore;

    @Override
    public void create() {
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
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.RED);
        lastSpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        bitmapFont.draw(batch, "Score: " + currentScore + "\nHigh Score: " + highScore, 50, GameDimensions.getLevelHeight() - 50);
        bitmapFont.getData().setScale(10);
        for (BaseCharacter character : characterArray) {
            boolean flip = character.isTextureNotPositionedCorrectly();
            float height = character.getRectangle().height;
            float width = character.getRectangle().width;
            batch.draw(character.getCharacterType().getTexture(), flip ? character.getX() + width : character.getX(), character.getY(), flip ? -width : width, height);
        }
        batch.end();

        performLevelActivities();
    }

    @Override
    public void dispose() {
        for (BaseCharacter character : characterArray) {
            character.getCharacterType().getTexture().dispose();
        }
        batch.dispose();
    }

    private void performLevelActivities(){
        checkForHit();
        moveCharacters();
        removeCharactersIfAppropriate();
        checkForClick();
        spawnBotIfAppropriate();
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
        if (currentScore > highScore)
            highScore = currentScore;
        currentScore = 0;
        eradicateAllBots();
        playerCharacter = new PlayerCharacter();
    }

    private void eradicateAllBots() {
        botArray.clear();
        for (BaseCharacter character : characterArray) {
            if (character instanceof HostileCharacter) {
                characterArray.removeValue(character, true);
            }
        }
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
    }
}
