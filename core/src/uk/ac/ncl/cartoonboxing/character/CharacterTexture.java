package uk.ac.ncl.cartoonboxing.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class CharacterTexture {
    private final Texture texture;
    private BaseCharacter.Direction direction;
    private final Sprite sprite;


    CharacterTexture(Texture texture, BaseCharacter.Direction direction) {
        this.texture = texture;
        this.direction = direction;
        sprite = new Sprite(texture);
    }

    public void flip(){
        sprite.flip(true, false);
        if (direction == BaseCharacter.Direction.LEFT)
            direction = BaseCharacter.Direction.RIGHT;
        else
            direction = BaseCharacter.Direction.LEFT;
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

