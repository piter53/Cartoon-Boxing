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
        characterType.getTexture().flip();
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
    public void keepPlayerCharacterWithinBounds() {
        if (getX()<0)
            setX(0);
        if (getX() > GameDimensions.getLevelWidth() - GameDimensions.getCharacterWidth())
            setX(GameDimensions.getLevelWidth() - GameDimensions.getCharacterWidth());
    }

}
