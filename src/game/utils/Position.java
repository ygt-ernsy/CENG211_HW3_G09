package game.utils;

import game.enums.Direction;

/**
 * Position
 */
public class Position {
    private int x;
    private int y;

    public Position(int x, int y){}
    public Position(Position other){} // Copy constructor
    public Position move(Direction dir){}
    public Position moveBack(Direction dir){}
    public boolean equals(Object obj){}
    public int hashCode(){}
    public int getX(){}
    public int getY(){}
    }
