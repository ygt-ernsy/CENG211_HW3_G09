package game.core;

import game.utils.Position;

/**
 * ITerrainObject
 */
public interface ITerrainObject {
    Position getPosition();

    void setPosition(Position pos);

    String getDisplaySymbol(); // Returns notation like "P1", "Kr", "HB"

    ITerrainObject deepCopy(); // For state management if needed
}
