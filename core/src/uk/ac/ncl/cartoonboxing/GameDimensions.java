package uk.ac.ncl.cartoonboxing;

import com.badlogic.gdx.Gdx;

/**
 * A class defining dimensions and ratios used throughout the game
 */
public final class GameDimensions {

    private static int levelWidth;
    private static int levelHeight;
    private static int characterWidth;
    private static int characterHeight;
    private static final double screenToCharacterWidthRatio = 0.15;

    private static GameDimensions instance;

    private GameDimensions(){
        update();
    }

    public static GameDimensions getInstance(){
        if (instance==null){
            instance = new GameDimensions();
        }
        return instance;
    }

    public static void update(){
        levelWidth = Gdx.graphics.getWidth();
        levelHeight = Gdx.graphics.getHeight();
        characterWidth = (int)(screenToCharacterWidthRatio * levelWidth);
        characterHeight = characterWidth;
    }

    public static int getLevelWidth() {
        return levelWidth;
    }

    public static int getLevelHeight() {
        return levelHeight;
    }

    public static int getCharacterWidth() {
        return characterWidth;
    }

    public static int getCharacterHeight() {
        return characterHeight;
    }
}
