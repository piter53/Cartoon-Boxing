package uk.ac.ncl.cartoonboxing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Class Level is to represent a current game level type. Most of the heavy lifting is done by LevelType enum, Level class acts more as a wrapper around it.
 * @author Piotr Grela
 */
public class Level {

    private final LevelType levelType;
    private final static LevelType defaultLevelType = LevelType.HILLS;

    public Level(LevelType levelType){
        this.levelType = levelType;
    }

    public Level(){
        this(defaultLevelType);
    }

    public enum LevelType{
        HILLS("Hills", 1, true, textureFromFile("background-mountains.jpg")),
        MOUNTAINS("Mountains", 2, true, textureFromFile("background-mountains.jpg")),
        CITY("City", 3, false, textureFromFile("background-mountains.jpg"));

        private final String name;
        private final int levelNumber;
        private final boolean hasClouds;
        private final Texture background;

        private static final String textureSubfolder = "backgrounds/";
        private static final ObjectMap<Integer, LevelType> levelNoToTypeMap = new ObjectMap<Integer, LevelType>();

        private static Texture textureFromFile (String filename){
            return new Texture(Gdx.files.internal(textureSubfolder+filename));
        }

        static {
            for (LevelType type : LevelType.values()){
                levelNoToTypeMap.put(type.levelNumber, type);
            }
        }

        LevelType(String name, int levelNo, boolean hasClouds, Texture background){
            this.name = name;
            this.levelNumber = levelNo;
            this.hasClouds = hasClouds;
            this.background = background;
        }

        public LevelType levelTypeByNumber(int levelNumber){
            return levelNoToTypeMap.get(levelNumber);
        }
    }
}
