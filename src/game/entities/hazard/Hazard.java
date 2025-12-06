package game.entities.hazard;

import game.core.IHazard;
import game.core.ITerrainObject;
import game.core.IcyTerrain;
import game.entities.penguin.Penguin;
import game.utils.Position;

/**
 * Hazard
 */
public abstract class Hazard implements ITerrainObject, IHazard {
    protected Position position;
    protected boolean plugged; // Relevant for HoleInIce, false for others

    public abstract void applyEffect(Penguin penguin, IcyTerrain terrain);
    public abstract String getDisplaySymbol();
}
