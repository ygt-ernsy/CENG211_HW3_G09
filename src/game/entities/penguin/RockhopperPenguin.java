package game.entities.penguin;

import java.util.ArrayList;

import game.core.IHazard;
import game.core.ITerrainObject;
import game.core.IcyTerrain;
import game.entities.hazard.HoleInIce;
import game.enums.Direction;
import game.utils.Position;

/**
 * RockhopperPenguin
 */
public class RockhopperPenguin extends Penguin {
    private boolean preparedToJump;
    private Position hazardToJump; // Which hazard will be jumped

    @Override
    public boolean useSpecialAction(Direction dir, IcyTerrain terrain) {
        preparedToJump = true;

        // Scan path for first hazard
        Position scanPos = new Position(position);
        while (true) {
            scanPos = scanPos.move(dir);

            if (!terrain.isPositionValid(scanPos)) break;

            ArrayList<ITerrainObject> objects = terrain.getObjectsAt(scanPos);
            for (ITerrainObject obj : objects) {
                if (obj instanceof IHazard && !(obj instanceof HoleInIce && ((HoleInIce) obj).isPlugged())) {
                    hazardToJump = new Position(scanPos);
                    specialActionUsed = true;
                    return true; // Found hazard to jump
                }
            }
        }

        specialActionUsed = true; // Used even if no hazard found (wasted)
        return false;
    }

    @Override
    public boolean shouldStopHere(Position pos) {
        return false;
    }

    // Special method called during slide to check if jump should occur
    public boolean shouldJumpAt(Position pos) {
        if (preparedToJump && hazardToJump != null) {
            return pos.equals(hazardToJump);
        }
        return false;
    }
}
