package game.entities.hazard;

import game.core.IcyTerrain;
import game.entities.penguin.Penguin;
import game.utils.Position;

/**
 * HeavyIceBlock
 */
public class HeavyIceBlock extends Hazard {
    @Override
    public void applyEffect(Penguin penguin, IcyTerrain terrain) {
        // Penguin stops before collision
        Position stopPos = penguin.getPosition().moveBack(penguin.getLastMoveDirection());
        penguin.setPosition(stopPos);

        // Penguin drops lightest food as penalty
        penguin.dropLightestFood();
    }

    @Override
    public String getDisplaySymbol() {
        return "HB";
    }
}
