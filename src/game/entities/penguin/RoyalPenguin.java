package game.entities.penguin;

import java.util.ArrayList;

import game.core.ITerrainObject;
import game.core.IcyTerrain;
import game.entities.food.Food;
import game.enums.Direction;
import game.utils.Position;

/**
 * RoyalPenguin
 */
public class RoyalPenguin extends Penguin {
    @Override
public boolean useSpecialAction(Direction dir, IcyTerrain terrain) {
    // Attempt to move one square in the given direction
    Position newPos = position.move(dir);

    if (!terrain.isPositionValid(newPos)) {
        // Accidentally steps into water
        hasFallen = true;
        terrain.removeObjectAt(position, this);
        specialActionUsed = true;
        return false;
    }

    // Check if landing spot is occupied
    ArrayList<ITerrainObject> objectsAtDest = terrain.getObjectsAt(newPos);

    // Can land if empty or only has food
    boolean canLand = objectsAtDest.stream()
        .allMatch(obj -> obj instanceof Food);

    if (canLand) {
        terrain.removeObjectAt(position, this);
        position = newPos;
        terrain.addObjectAt(position, this);

        // Collect any food at this position
        for (ITerrainObject obj : new ArrayList<>(objectsAtDest)) {
            if (obj instanceof Food) {
                collectFood((Food) obj);
                terrain.removeObjectAt(newPos, obj);
            }
        }
        
        specialActionUsed = true;
        return true;
    }

    specialActionUsed = true;
    return false;
}

@Override
public boolean shouldStopHere(Position pos) {
    return false; // Royal penguins don't have voluntary stopping
}
}
