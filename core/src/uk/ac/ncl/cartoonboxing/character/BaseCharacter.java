package uk.ac.ncl.cartoonboxing.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Random;

import uk.ac.ncl.cartoonboxing.GameDimensions;

/**
 * Class defining a base character of the game, including pre-defined character classes,
 * and character generator.
 * @author Piotr Grela
 * @version 1.0
 */
public abstract class BaseCharacter {
    CharacterType characterType;
    Rectangle rectangle;
    Direction movingDirection;

    /**
     * An enum representing moving direction of the character. It provides a method to pick the direction randomly.
     */
    public enum Direction {
        LEFT,
        RIGHT;

        private static final Random random = new Random();

        public static Direction getRandomDirection() {
            if (random.nextBoolean())
                return Direction.RIGHT;
            else
                return Direction.LEFT;
        }


    }

    /**
     * An enum for pre-defined character types, that include various movement speeds, and names.
     */
    public enum CharacterType {
        VERY_SLOW_BOI("Very slow boi", 0, 0.1, textureFromFile("boxer-dude.png"), Direction.RIGHT),
        SLOW_BOI("Slow boi", 1, 0.15, textureFromFile("boxer-dude.png"), Direction.RIGHT),
        MEDIOCRE_BOI("Mediocre boi", 2, 0.3, textureFromFile("boxer-dude.png"), Direction.RIGHT),
        FAST_BOI("Fast boi", 3, 0.9, textureFromFile("boxer-dude.png"), Direction.RIGHT);
        private final String name;

        private final int ID;
        private final double SPEED;
        private final CharacterTexture characterTexture;
        private static final String TEXTURE_SUBFOLDER = "characters/";
        private static final CharacterType DEFAULT_CHARACTER_TYPE = CharacterType.VERY_SLOW_BOI;

        private static final ObjectMap<Double,CharacterType> speedToTypeMap = new ObjectMap<Double, CharacterType>();
        static {
            for (CharacterType type : CharacterType.values()){
                speedToTypeMap.put(type.SPEED, type);
            }
        }

        CharacterType(String name, int id, double speed, Texture texture, Direction textureDirection){
            this.name = name;
            this.ID = id;
            this.SPEED = speed;
            this.characterTexture = new CharacterTexture(texture, textureDirection);
        }

        public static CharacterType randomType(double maxSpeed) {
            FloatArray array = new FloatArray();
            for (Double speed : speedToTypeMap.keys()){
                if (speed<=maxSpeed){
                    array.add((float)speed.doubleValue());
                }
            }
            Double randomSpeed = (double)array.random();
            CharacterType type = speedToTypeMap.get(randomSpeed);
            if (type == null) {
                type = DEFAULT_CHARACTER_TYPE;
            }
            return type;
        }
        private static Texture textureFromFile (String filename){
            return new Texture(Gdx.files.internal(TEXTURE_SUBFOLDER +filename));
        }

        public static CharacterType randomType(){
            return randomType(1.0);
        }

        public String getName() {
            return name;
        }

        public int getID() {
            return ID;
        }

        public double getSpeed() {
            return SPEED;
        }

        public CharacterTexture getTexture() {
            return characterTexture;
        }

        public static CharacterType getDefaultCharacterType() {
            return DEFAULT_CHARACTER_TYPE;
        }

    }

    public BaseCharacter(CharacterType characterType){
        this.characterType = characterType;
        rectangle = new Rectangle();
        rectangle.height = GameDimensions.getCharacterHeight();
        rectangle.width = GameDimensions.getCharacterWidth();
        movingDirection = Direction.getRandomDirection();
        if (movingDirection != characterType.getTexture().getDirection()) {
            characterType.getTexture().flip();
        }
    }

    public BaseCharacter() {
        this(CharacterType.DEFAULT_CHARACTER_TYPE);
    }

    public boolean isOutOfBounds() {
        return !(getX() >= 0) || !(getX() < GameDimensions.getLevelWidth() - GameDimensions.getCharacterWidth());
    }

    public float getX() {
        return getSprite().getX();
    }

    public CharacterType getCharacterType() {
        return characterType;
    }

    public CharacterTexture getCharacterTexture() {
        return characterType.getTexture();
    }

    public Direction getMovingDirection() {
        return movingDirection;
    }

    public Sprite getSprite() {
        return characterType.getTexture().getSprite();
    }

    public void setX(float x) {
        getSprite().setX(x);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
