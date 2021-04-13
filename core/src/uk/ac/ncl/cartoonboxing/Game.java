package uk.ac.ncl.cartoonboxing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.compression.lzma.Base;

public class Game extends ApplicationAdapter {
	private Texture characterTexture;
	private Texture levelBackground;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Rectangle characterModel;

    private int levelWidth;
    private int levelHeight;

    private int characterWidth;
    private int characterHeight;

    private int currentScore;

    @Override
	public void create () {
        levelWidth = Gdx.graphics.getWidth();
        levelHeight = Gdx.graphics.getHeight();
        characterWidth = (int)(0.15 * levelWidth);
        characterHeight = characterWidth;
		characterTexture = new Texture(Gdx.files.internal("characters/boxer-dude.png"));
		levelBackground = new Texture(Gdx.files.internal("backgrounds/background-mountains.jpg"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        batch = new SpriteBatch();

        characterModel = new Rectangle();
        characterModel.x = levelWidth /2 - characterWidth /2;
        characterModel.y = 0;
        characterModel.width = characterWidth;
        characterModel.height = characterHeight;
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(characterTexture, characterModel.x, characterModel.y);
        batch.end();
    }

	@Override
	public void dispose () {
	}

	private void spawnNewBot(){
        BaseCharacter character = BaseCharacter.generateRandomCharacter(currentScore);
    }

    public int getCurrentScore() {
        return currentScore;
    }
}
