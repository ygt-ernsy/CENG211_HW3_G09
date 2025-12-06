package game.entities.penguin;

import game.core.IcyTerrain;
import game.enums.Direction;

/**
 * EmperorPenguin
 */
public class EmperorPenguin extends Penguin {
    private int slideCount;

    @Override
    public boolean useSpecialAction(Direction dir, IcyTerrain terrain) {
        specialActionUsed = true;
        slideCount = 0;
        return true;
    }
    @Override
    public boolean shouldStopHere(Position pos) {
        if (specialActionUsed && !hasFallen) {
            slideCount++;
        if (slideCount >= 3) {
            return true; // Stop at 3rd square
        }
        }
        return false;
    }
}
