package uk.ac.ncl.cartoonboxing.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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
    private CharacterType characterType;
    private Rectangle model;
    static final CharacterType DEFAULT_CHARACTER_TYPE = CharacterType.VERY_SLOW_BOI;
    private Direction movingDirection;

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
        VERY_SLOW_BOI("Very slow boi", 0, 0.1, textureFromFile("boxer-dude.png")),
        SLOW_BOI("Slow boi", 1, 0.15, textureFromFile("boxer-dude.png")),
        MEDIOCRE_BOI("Mediocre boi", 2, 0.3, textureFromFile("boxer-dude.png")),
        FAST_BOI("Fast boi", 3, 0.9, textureFromFile("boxer-dude.png"));

        private final String name;
        private final int id;
        private final double speed;
        private final Texture texture;

        private static final String textureSubfolder = "characters/";

        private static final ObjectMap<Double,CharacterType> speedToTypeMap = new ObjectMap<Double, CharacterType>();
        static {
            for (CharacterType type : CharacterType.values()){
                speedToTypeMap.put(type.speed, type);
            }
        }

        CharacterType(String name, int id, double speed, Texture texture){
            this.name = name;
            this.id = id;
            this.speed = speed;
            this.texture = texture;
        }

        public static CharacterType randomType(double maxSpeed) {
            FloatArray array = new FloatArray();
            for (Double speed : speedToTypeMap.keys()){
                if (speed<=maxSpeed){
                    array.add((float)speed.doubleValue());
                }
            }
            Double randomSpeed = (double)array.random();
            return speedToTypeMap.get(randomSpeed);
        }

        private static Texture textureFromFile (String filename){
            return new Texture(Gdx.files.internal(textureSubfolder+filename));
        }

        public static CharacterType randomType(){
            return randomType(1.0);
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public double getSpeed() {
            return speed;
        }

        public Texture getTexture() {
            return texture;
        }

    }

    public BaseCharacter(CharacterType characterType){
        model = new Rectangle();
        model.height = GameDimensions.getCharacterHeight();
        model.width = GameDimensions.getCharacterWidth();
        movingDirection = Direction.getRandomDirection();
        this.characterType = characterType;
    }

    public BaseCharacter() {
        this(DEFAULT_CHARACTER_TYPE);
    }

    public void setX(float x) {
        model.x = x;
    }

    /**
     * Change moving direction to make character go the other way
     */
    public void changeMovingDirection(){
        if (movingDirection == Direction.LEFT)
            movingDirection = Direction.RIGHT;
        else
            movingDirection = Direction.LEFT;
    }

    public float getX() {
        return model.x;
    }

    public float getY() {
        return model.y;
    }

    public CharacterType getCharacterType() {
        return characterType;
    }

    public Texture getCharacterTexture() {
        return characterType.texture;
    }

    public void setMovingDirection(Direction movingDirection) {
        this.movingDirection = movingDirection;
    }

    public boolean isOutOfBounds() {
        return !(getX() >= 0) || !(getX() < GameDimensions.getLevelWidth() - GameDimensions.getCharacterWidth());
    }

    public Direction getMovingDirection() {
        return movingDirection;
    }
}
