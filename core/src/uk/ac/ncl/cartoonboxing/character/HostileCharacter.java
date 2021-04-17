package uk.ac.ncl.cartoonboxing.character;

import uk.ac.ncl.cartoonboxing.GameDimensions;

public class HostileCharacter extends BaseCharacter {

    private static final int DIFFICULTY_SPEED_RATIO = 100;

    public HostileCharacter(CharacterType characterType) {
        super(characterType);
        if (getMovingDirection() == Direction.LEFT) {
            setX(getRightmostSpawnX());
        } else {
            setX(getLeftmostSpawnX());
        }
    }

    private float getRightmostSpawnX() {
        return GameDimensions.getLevelWidth();
    }

    private float getLeftmostSpawnX() {
        return - getWidth();
    }

    /**
     *
     * @param difficulty defines a threshold that a generated random character should not exceed
     * @return BaseCharacter with random CharacterType
     */
    public static HostileCharacter generateRandomCharacter(int difficulty){
        return new HostileCharacter(CharacterType.randomType(difficulty/ DIFFICULTY_SPEED_RATIO));
    }

    @Override
    public boolean isOutOfBounds() {
        return !((getX() >= - getWidth()) && (getX() <= GameDimensions.getLevelWidth()));
    }

}
