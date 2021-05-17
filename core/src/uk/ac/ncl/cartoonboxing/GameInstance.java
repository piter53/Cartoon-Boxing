package uk.ac.ncl.cartoonboxing;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameInstance extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    private Screen screen;

    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // use libGDX's default Arial font
        screen = new uk.ac.ncl.cartoonboxing.Game(this);
        this.setScreen(screen);
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        screen.dispose();
    }
}
