package game.entities.penguin;

import game.core.IcyTerrain;
import game.enums.Direction;
import game.utils.Position;

/**
 * KingPenguin
 */
public class KingPenguin extends Penguin {
    private int slideCount; // Tracks squares slid in current movement

    @Override
    public boolean useSpecialAction(Direction dir, IcyTerrain terrain) {
    specialActionUsed = true;
    slideCount = 0; // Reset counter
    // The actual stopping logic is in shouldStopHere()
    return true;
    }
    
    @Override
    public boolean shouldStopHere(Position pos) {
        if (specialActionUsed && !hasFallen) {
            slideCount++;
        
        if (slideCount >= 5) {
            return true; // Stop at 5th square
        }
    }
    return false;
    }
}
