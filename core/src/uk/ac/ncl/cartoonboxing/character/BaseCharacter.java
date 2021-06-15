package uk.ac.ncl.cartoonboxing.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import uk.ac.ncl.cartoonboxing.GameDimensions;

/**
 * Class defining a base character of the game, including pre-defined character classes,
 * and character generator.
 * @author Piotr Grela
 * @version 1.0
 */
public abstract class BaseCharacter {

    @Getter
    final CharacterType characterType;
    @Getter
    Direction movingDirection;
    @Getter
    final Rectangle rectangle;
    private static Random random = new Random();

    /**
     * An enum representing moving direction of the character. It provides a method to pick the direction randomly.
     */
    public enum Direction {
        LEFT,
        RIGHT;

        private static final Random random = new Random();

        public static Direction getOppositeDirection(Direction direction){
            if (direction == Direction.LEFT) {
                return Direction.RIGHT;
            } else {
                return Direction.LEFT;
            }
        }

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
        VERY_SLOW_BOI("Very slow boi", 0, 0.15, textureFromFile("boxer-dude-blue.png"), Direction.RIGHT),
        SLOW_BOI("Slow boi", 1, 0.3, textureFromFile("boxer-dude-yellow.png"), Direction.RIGHT),
        MEDIOCRE_BOI("Mediocre boi", 2, 0.4, textureFromFile("boxer-dude-green.png"), Direction.RIGHT),
        FAST_BOI("Fast boi", 3, 0.55, textureFromFile("boxer-dude-red.png"), Direction.RIGHT),
        VERY_FAST_BOI("Very fast boi", 4, 0.70, textureFromFile("boxer-dude-black.png"), Direction.RIGHT),
        THE_SPECIMEN("The specimen", 5, 1.0, textureFromFile("boxer-dude-white.png"), Direction.RIGHT);

        @Getter
        private final String name;
        @Getter
        private final int ID;
        @Getter
        private final Texture texture;
        @Getter
        private final Direction textureDirection;

        @Getter
        private final double SPEED;
        private static final String TEXTURE_SUBFOLDER = "characters/";
        @Getter
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
            this.texture = texture;
            this.textureDirection = textureDirection;
        }
        public static CharacterType randomType(double maxSpeed) {
            ArrayList<Double> array = new ArrayList<>();
            Array<Double> keys = speedToTypeMap.keys().toArray();
            for (Double speed : keys){
                if (speed<=maxSpeed){
                    array.add(speed);
                }
            }
            Double randomSpeed = null;
            CharacterType type = null;
            if (!array.isEmpty()) {
                randomSpeed = array.get(random.nextInt(array.size()));
                type = speedToTypeMap.get(randomSpeed);
            }
            if (type == null) {
                type = DEFAULT_CHARACTER_TYPE;
            }
            return type;
        }

        public static CharacterType randomType(){
            return randomType(1.0);
        }

        private static Texture textureFromFile (String filename){
            return new Texture(Gdx.files.internal(TEXTURE_SUBFOLDER +filename));
        }

    }

    public BaseCharacter(CharacterType characterType){
        this.characterType = characterType;
        movingDirection = Direction.getRandomDirection();
        rectangle = new Rectangle();
        rectangle.setY(GameDimensions.getCharacterStartingY());
        rectangle.width = GameDimensions.getDefaultCharacterWidth();
        rectangle.height = GameDimensions.getDefaultCharacterHeight();
        setStartingCoordinates();
    }

    public boolean isTextureNotPositionedCorrectly() {
        return movingDirection != characterType.textureDirection;
    }

    public boolean inFrontOf(BaseCharacter character) {
        return character.getX() > this.getX() && character.getMovingDirection() == Direction.LEFT
            || character.getX() < this.getX() && character.getMovingDirection() == Direction.RIGHT;
    }

    public float getY() {
        return rectangle.getY();
    }

    public float getX() {
        return rectangle.getX();
    }

    void setX(float x) {
        rectangle.setX(x);
    }

    float getWidth() {
        return rectangle.getWidth();
    }

    float getHeight() {
        return rectangle.getHeight();
    }

    public void updateX(int globalSpeed, float deltaTime){
        if (getMovingDirection() == BaseCharacter.Direction.LEFT) {
            setX((float) (getX() - globalSpeed * getCharacterType().SPEED * deltaTime));
        } else {
            setX((float) (getX() + globalSpeed * getCharacterType().SPEED * deltaTime));
        }

    }

    abstract void setStartingCoordinates();

    public abstract boolean isOutOfBounds();

    public abstract boolean handleOutOfBounds();

    public abstract boolean isAtBoundary();
}
