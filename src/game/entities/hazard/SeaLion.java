package game.entities.hazard;

import game.core.IcyTerrain;
import game.entities.penguin.Penguin;
import game.enums.Direction;

/**
 * SeaLion
 */
public class SeaLion extends Hazard {
    @Override
    public void applyEffect(Penguin penguin, IcyTerrain terrain) {
        Direction originalDir = penguin.getLastMoveDirection();
        Direction bouncedDir = originalDir.getOpposite();

        // Penguin bounces in opposite direction
        terrain.processPenguinSlide(penguin, bouncedDir);

        // SeaLion slides in original direction
        terrain.processHazardSlide(this, originalDir);
    }

    public boolean canSlide() {
        return true;
    }

    public void initiateSlide(Direction dir, IcyTerrain terrain) {
        terrain.processHazardSlide(this, dir);
    }

    @Override
    public String getDisplaySymbol() {
        return "SL";
    }
}
