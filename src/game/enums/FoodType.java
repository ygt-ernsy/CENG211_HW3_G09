package game.enums;

/**
 * FoodType
 */
public enum FoodType {
    KRILL("Kr"),
    CRUSTACEAN("Cr"),
    ANCHOVY("An"),
    SQUID("Sq"),
    MACKEREL("Ma");

    private final String symbol;
    public String getSymbol();
    public static FoodType random(); // Returns random food type
}
