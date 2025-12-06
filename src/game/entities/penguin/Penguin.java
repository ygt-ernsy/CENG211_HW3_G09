package game.entities.penguin;

import java.util.ArrayList;

import game.core.ISlidable;
import game.core.ITerrainObject;
import game.core.IcyTerrain;
import game.entities.food.Food;
import game.enums.Direction;
import game.enums.PenguinType;
import game.utils.Position;

/**
 * Penguin
 */
public abstract class Penguin implements ITerrainObject, ISlidable {
    protected String name; // "P1", "P2", "P3"
    protected PenguinType type;
    protected Position position;
    protected ArrayList<Food> collectedFood;
    protected boolean specialActionUsed;
    protected boolean isStunned; // For LightIceBlock collision
    protected boolean hasFallen; // Fell into water or hole
    protected boolean isPlayerControlled;

    // Abstract methods that subclasses MUST implement (enforces polymorphism)
    public abstract boolean useSpecialAction(Direction dir, IcyTerrain terrain);
    public abstract boolean shouldStopHere(Position pos); // For King/Emperor abilities

    // Concrete methods shared by all penguins
    public void collectFood(Food food){}
    public void dropLightestFood(){}
    public int calculateTotalWeight(){}
    public void slide(Direction dir, IcyTerrain terrain){}
    public boolean canSlide(){}
    public void initiateSlide(Direction dir, IcyTerrain terrain){}
    public String getDisplaySymbol(){}
    public ArrayList<Food> getCollectedFood(){}
}
