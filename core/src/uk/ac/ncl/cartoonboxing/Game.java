package uk.ac.ncl.cartoonboxing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import uk.ac.ncl.cartoonboxing.character.BaseCharacter;
import uk.ac.ncl.cartoonboxing.character.HostileCharacter;
import uk.ac.ncl.cartoonboxing.character.PlayerCharacter;

/**
 * Main class of the game that controls rendering, management of game entities (characters, levels, etc.)
 * @author Piotr Grela
 */
public class Game extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Level currentLevel;
    private PlayerCharacter playerCharacter;
    private Array<BaseCharacter> characterArray;
    private final int MOVING_SPEED_PX = 1000;
    private long lastSpawnTime;
    private final long SPAWN_DELTA_TIME = 10000000000L;

    private int currentScore;

    @Override
	public void create () {
        currentScore = 0;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        batch = new SpriteBatch();
        currentLevel = new Level();
        characterArray = new Array<BaseCharacter>();
        GameDimensions.update();
        playerCharacter = new PlayerCharacter();
        characterArray.add(playerCharacter);
    }

	@Override
	public void render () {
        Gdx.gl.glClearColor(0, 0, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
//        playerCharacter.getSprite().draw(batch);
        for (BaseCharacter character : characterArray) {
            character.getSprite().setX(character.getX());
            character.getSprite().draw(batch);
        }
        batch.end();

//        checkForHit();
        checkForClick();
        moveCharacters();
        playerCharacter.keepPlayerCharacterWithinBounds();
        spawnBotIfAppropriate();
    }

	@Override
	public void dispose () {
        for (BaseCharacter character : characterArray) {
            character.getCharacterType().getTexture().dispose();
        }
        batch.dispose();
	}

	private void checkForClick() {
        if (Gdx.input.justTouched()) {
            playerCharacter.flipCharacter();
        }
    }

    private void checkForHit(){
        for (BaseCharacter character : characterArray) {
            if (!(character instanceof PlayerCharacter)) {
                if (character.getRectangle().overlaps(playerCharacter.getRectangle())) {
                    if (character.getX() < playerCharacter.getX() && playerCharacter.getMovingDirection() == BaseCharacter.Direction.LEFT
                        || character.getX() > playerCharacter.getX() && playerCharacter.getMovingDirection() == BaseCharacter.Direction.RIGHT) {
                        characterArray.removeValue(character,true);
                    }
                }
            }
        }
    }

    /**
     * Method responsible for moving all characters stored in the array, and removing those that are out of level bounds. 
     * Amount of movement of each character is based on their pre-defined speed, and movingSpeedPx constant
     */
    private void moveCharacters() {
        for (BaseCharacter character : characterArray) {
            float characterSpeed = (float)character.getCharacterType().getSpeed();
            float deltaTime = Gdx.graphics.getDeltaTime();
            float characterX = character.getX();
            if (character.isOutOfBounds() && character instanceof HostileCharacter) {
                characterArray.removeValue(character, true);
            }
            else {
                if (character.getMovingDirection() == BaseCharacter.Direction.LEFT) {
                    character.setX(characterX - MOVING_SPEED_PX * characterSpeed * deltaTime);
                } else {
                    character.setX(characterX + MOVING_SPEED_PX * characterSpeed * deltaTime);
                }
            }
        }
    }

	private void spawnNewBot(){
        HostileCharacter character = HostileCharacter.generateRandomCharacter(currentScore);
        characterArray.add(character);
        lastSpawnTime = TimeUtils.nanoTime();
    }

    private void spawnBotIfAppropriate(){
        if (TimeUtils.nanoTime() - lastSpawnTime > SPAWN_DELTA_TIME/(currentScore+1)){
            spawnNewBot();
        }
    }

    public int getCurrentScore() {
        return currentScore;
    }
}
