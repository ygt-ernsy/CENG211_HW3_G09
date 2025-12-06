package game.core;

/**
 * ITerrainObject
 */
public interface ITerrainObject {
    Position getPosition();

    void setPosition(Position pos);

    String getDisplaySymbol(); // Returns notation like "P1", "Kr", "HB"

    ITerrainObject deepCopy(); // For state management if needed
}
