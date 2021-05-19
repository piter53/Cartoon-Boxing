package uk.ac.ncl.cartoonboxing.character;

import uk.ac.ncl.cartoonboxing.GameDimensions;

/**
 * This class defines a character controller by the user
 * @author Piotr Grela
 */
public class PlayerCharacter extends BaseCharacter {

    private final static CharacterType DEFAULT_CHARACTER = CharacterType.MEDIOCRE_BOI;

    public PlayerCharacter(CharacterType characterType) {
        super(characterType);
    }

    public PlayerCharacter() {
        this(DEFAULT_CHARACTER);
    }

    public void flipCharacter(){
        movingDirection = Direction.getOppositeDirection(movingDirection);
    }

    /**
     * A function to make sure that the playerCharacter stays within screen bounds
     */
    @Override
    public boolean isOutOfBounds() {
        return !((getX() >= 0)&&(getX() <= GameDimensions.getLevelWidth() - getWidth()));
    }

    @Override
    void setStartingCoordinates() {
        // set X-coordinate to the middle of the screen
        setX(GameDimensions.getMiddleSpawnX());
    }

    @Override
    public boolean handleOutOfBounds() {
        if (getX() < 0)
            setX(0);
        else
            setX(GameDimensions.getLevelWidth() - getWidth());
        return true;
    }
}
