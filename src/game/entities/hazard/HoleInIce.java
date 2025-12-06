package game.entities.hazard;

import game.core.ITerrainObject;
import game.core.IcyTerrain;
import game.entities.penguin.Penguin;

/**
 * HoleInIce
 */
public class HoleInIce extends Hazard {
    private boolean plugged;

    @Override
    public void applyEffect(Penguin penguin, IcyTerrain terrain) {
        if (!plugged) {
            // Penguin falls into hole
            penguin.setFallen(true);
            terrain.removePenguinFromGame(penguin);
        } else {
            // Penguin passes over plugged hole safely
            // Continue sliding
        }
    }

    public void plugHole(ITerrainObject obj) {
        if (obj instanceof LightIceBlock || obj instanceof SeaLion) {
            plugged = true;
            // Remove the object that plugged it
        }
    }

    @Override
    public String getDisplaySymbol() {
        return plugged ? "PH" : "HI";
    }
}
