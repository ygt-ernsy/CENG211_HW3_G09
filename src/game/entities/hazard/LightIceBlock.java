package game.entities.hazard;

import game.core.IcyTerrain;
import game.entities.penguin.Penguin;
import game.enums.Direction;

/**
 * LightIceBlock
 */
public class LightIceBlock extends Hazard{
    @Override
    public void applyEffect(Penguin penguin, IcyTerrain terrain) {
        // Penguin stays at collision position
        penguin.setStunned(true); // Next turn skipped

        // This ice block starts sliding
        Direction slideDir = penguin.getLastMoveDirection();
        terrain.processHazardSlide(this, slideDir);
    }

    @Override
    public boolean canSlide() {
        return true;
    }

    @Override
    public void initiateSlide(Direction dir, IcyTerrain terrain) {
        terrain.processHazardSlide(this, dir);
    }

    @Override
    public String getDisplaySymbol() {
        return "LB";
    }
}
