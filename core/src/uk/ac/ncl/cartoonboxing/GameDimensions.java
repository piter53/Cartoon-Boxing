package uk.ac.ncl.cartoonboxing;

import com.badlogic.gdx.Gdx;

/**
 * A class defining dimensions and ratios used throughout the game
 */
public final class GameDimensions {

    private static int levelWidth;
    private static int levelHeight;
    private static int defaultCharacterWidth;
    private static int defaultCharacterHeight;
    private static final double screenToCharacterWidthRatio = 0.2;
    private static float middleSpawnX;
    private static float leftmostSpawnX;
    private static float rightmostSpawnX;
    private static final float characterStartingY = 0;

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
        defaultCharacterWidth = (int)(screenToCharacterWidthRatio * levelWidth);
        defaultCharacterHeight = (int)(defaultCharacterWidth * 1.5);
        middleSpawnX = (levelWidth - defaultCharacterWidth)/2f;
        leftmostSpawnX = 0;
        rightmostSpawnX = levelWidth - defaultCharacterWidth;
    }

    public static int getLevelWidth() {
        return levelWidth;
    }

    public static int getLevelHeight() {
        return levelHeight;
    }

    public static int getDefaultCharacterWidth() {
        return defaultCharacterWidth;
    }

    public static int getDefaultCharacterHeight() {
        return defaultCharacterHeight;
    }

    public static float getMiddleSpawnX() {
        return middleSpawnX;
    }

    public static float getLeftmostSpawnX() {
        return leftmostSpawnX;
    }

    public static float getRightmostSpawnX() {
        return rightmostSpawnX;
    }

    public static float getCharacterStartingY() {
        return characterStartingY;
    }
}
