package game.core;

import game.entities.penguin.Penguin;
import game.enums.Direction;

public interface IHazard {
    void applyEffect(Penguin penguin, IcyTerrain terrain);
    boolean isPlugged(); // Relevant for HoleInIce
    void setPlugged(boolean plugged);
}