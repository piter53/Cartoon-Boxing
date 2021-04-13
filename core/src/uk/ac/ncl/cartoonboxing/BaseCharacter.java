package uk.ac.ncl.cartoonboxing;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.SortedIntList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BaseCharacter {
    private final CharacterType characterType;
    private static final int difficultySpeedRatio = 100;

    public enum CharacterType {
        VERY_SLOW_BOI("Very slow boi", 0, 0.1),
        SLOW_BOI("Slow boi", 1, 0.15),
        MEDIOCRE_BOI("Mediocre boi", 2, 0.3),
        FAST_BOI("Fast boi", 3, 0.9);

        private final String name;

        private final int id;
        private final double speed;
        private static final ObjectMap<Double,CharacterType> speedToTypeMap = new ObjectMap<Double, CharacterType>();

        private CharacterType(String name, int id, double speed){
            this.name = name;
            this.id = id;
            this.speed = speed;
        }

        static {
            for (CharacterType type : CharacterType.values()){
                speedToTypeMap.put(type.speed, type);
            }
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

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public double getSpeed() {
            return speed;
        }

    }
    public BaseCharacter(CharacterType characterType){
        this.characterType = characterType;
    }

    public static BaseCharacter generateRandomCharacter(int difficulty){
        return new BaseCharacter(CharacterType.randomType(difficulty/difficultySpeedRatio));
    }

    public CharacterType getCharacterType() {
        return characterType;
    }
}
