package game.entities.food;

import game.enums.FoodType;
import game.utils.Position;

/**
 * Food
 */
public class Food {
    private FoodType foodType;
    private int weight; // 1-5 units
    private Position position;

    public FoodType getFoodType(){}
    public int getWeight(){}
    public String getDisplaySymbol(){} // Returns "Kr", "Cr", "An", "Sq", "Ma"
}
