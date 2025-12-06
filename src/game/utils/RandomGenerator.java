package game.utils;

import java.util.List;
import java.util.Random;

/**
 * RandomGenerator
 */
public class RandomGenerator {
    private static Random random = new Random();

    public static int nextInt(int bound){}
    public static boolean chance(double probability){} // Returns true with given probability
    public static <T> T randomElement(List<T> list){}
    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass){}
}
