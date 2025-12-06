package game.core;

import game.enums.Direction;

public interface ISlidable {
    boolean canSlide();
    void initiateSlide(Direction direction, IcyTerrain terrain);
}