package game;

import game.core.IcyTerrain;

public class SlidingPuzzleApp {

    public static void main(String[] args) {
    System.out.println("Welcome to Sliding Penguins Puzzle Game App.");
    System.out.println("An 10x10 icy terrain grid is being generated.");
    System.out.println("Penguins, Hazards, and Food items are also being generated.");
    System.out.println();
    }
    IcyTerrain terrain = new IcyTerrain();
    terrain.startGame();
}