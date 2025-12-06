package game.enums;

/**
 * PenguinType
 */
public class PenguinType {
    KING("King Penguin"),
    EMPEROR("Emperor Penguin"),
    ROYAL("Royal Penguin"),
    ROCKHOPPER("Rockhopper Penguin");

    private final String displayName;
    public String getDisplayName();
    public static PenguinType random();
}
