package uk.ac.ncl.cartoonboxing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

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
    private final int MOVING_SPEED_PX = 10000;

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
        batch.draw(playerCharacter.getCharacterTexture(), playerCharacter.getX(), playerCharacter.getY());
        batch.end();

        if (Gdx.input.justTouched()) {
            playerCharacter.changeMovingDirection();
        }
        moveCharacters();
        playerCharacter.keepPlayerCharacterWithinBounds();
    }

	@Override
	public void dispose () {
	}

    /**
     * Method responsible for moving all characters stored in the array, and removing those that are out of level bounds. 
     * Amount of movement of each character is based on their pre-defined speed, and movingSpeedPx constant
     */
    private void moveCharacters() {
        for (Iterator<BaseCharacter> iter = characterArray.iterator(); iter.hasNext(); ) {
            BaseCharacter character = iter.next();
            float characterSpeed = (float)character.getCHARACTER_TYPE().getSpeed();
            float deltaTime = Gdx.graphics.getDeltaTime();
            float characterX = character.getX();
            if (character.getMovingDirection() == BaseCharacter.Direction.LEFT) {
                character.setX(characterX - MOVING_SPEED_PX * characterSpeed * deltaTime);
            } else {
                character.setX(characterX + MOVING_SPEED_PX * characterSpeed * deltaTime);
            }
            if (character.isOutOfBounds() && !(character instanceof BaseCharacter)) {
                iter.remove();
            }
        }
    }

	private void spawnNewBot(){
        BaseCharacter character = BaseCharacter.generateRandomCharacter(currentScore);
    }

    public int getCurrentScore() {
        return currentScore;
    }
}
