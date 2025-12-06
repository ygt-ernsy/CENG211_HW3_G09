package game.enums;

/**
 * Direction
 */
public enum Direction {
    UP, 
    DOWN, 
    LEFT, 
    RIGHT;

    public Direction getOpposite(); // UP ↔ DOWN, LEFT ↔ RIGHT
    public int getDeltaX(); // Returns -1, 0, or 1
    public int getDeltaY(); // Returns -1, 0, or 1
    public static Direction fromString(String input); // Parses "U", "D", "L","R"
}
