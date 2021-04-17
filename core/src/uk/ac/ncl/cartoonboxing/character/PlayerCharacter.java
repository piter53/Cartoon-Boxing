package uk.ac.ncl.cartoonboxing.character;

import uk.ac.ncl.cartoonboxing.GameDimensions;

/**
 * This class defines a character controller by the user
 * @author Piotr Grela
 */
public class PlayerCharacter extends BaseCharacter {

    private final static CharacterType DEFAULT_CHARACTER = CharacterType.FAST_BOI;

    public PlayerCharacter(CharacterType characterType) {
        super(characterType);
        // set X-coordinate to the middle of the screen
        setX(GameDimensions.getMiddleSpawnX());
    }

    public PlayerCharacter() {
        this(DEFAULT_CHARACTER);
    }

    public void flipCharacter(){
        getCharacterTexture().flip();
        if (movingDirection == Direction.LEFT) {
            movingDirection = Direction.RIGHT;
        }
        else{
            movingDirection = Direction.LEFT;
        }
    }

    /**
     * A function to make sure that the playerCharacter stays within screen bounds
     */
    @Override
    public boolean isOutOfBounds() {
        return !((getX() >= 0)&&(getX() <= GameDimensions.getLevelWidth() - getWidth()));
    }

    public void keepPlayerCharacterWithinBounds() {
        if (isOutOfBounds()) {
            if (getX() < 0)
                setX(0);
            else
                setX(GameDimensions.getLevelWidth() - getWidth());
        }
    }

}
