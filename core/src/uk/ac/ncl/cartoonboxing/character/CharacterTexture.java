package uk.ac.ncl.cartoonboxing.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import uk.ac.ncl.cartoonboxing.GameDimensions;

public class CharacterTexture {
    private final Texture texture;
    private BaseCharacter.Direction direction;
    private final Sprite sprite;


    CharacterTexture(BaseCharacter.CharacterType characterType, BaseCharacter.Direction direction) {
        this.texture = characterType.getTexture();
        this.direction = direction;
        sprite = new Sprite(characterType.getTexture(), GameDimensions.getDefaultCharacterWidth(), GameDimensions.getDefaultCharacterHeight());
        if (this.direction != characterType.getTextureDirection()){
            sprite.flip(true, false);
        }
    }

    public void flip(){
        sprite.flip(true, false);
        if (direction == BaseCharacter.Direction.LEFT)
            direction = BaseCharacter.Direction.RIGHT;
        else
            direction = BaseCharacter.Direction.LEFT;
    }

    public float getHeight(){
        return sprite.getHeight();
    }

    public float getWidth() {
        return sprite.getHeight();
    }

    public void setX(float x) {
        sprite.setX(x);
    }

    public void setY(float y) {
        sprite.setY(y);
    }

    public float getY() {
        return sprite.getY();
    }

    public float getX () {
        return sprite.getX();
    }

    public Texture getTexture() {
        return texture;
    }

    public BaseCharacter.Direction getDirection() {
        return direction;
    }

    public Sprite getSprite() {
        return sprite;
    }
}

