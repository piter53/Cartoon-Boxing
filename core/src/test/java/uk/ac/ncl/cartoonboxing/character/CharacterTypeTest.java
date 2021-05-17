package uk.ac.ncl.cartoonboxing.character;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterTypeTest {

    @Test
    public void testRandomType() {
        ArrayList<BaseCharacter.CharacterType> list = new ArrayList<>();
        for (double i = 0.1; i <= 1.0; i+=0.05) {
            for (int j = 0; j < 100; j++) {
                list.add(BaseCharacter.CharacterType.randomType(i));
            }
            for (BaseCharacter.CharacterType type : list) {
                assertTrue(type.getSPEED() <= i);
            }
            list.clear();
        }
    }
}
