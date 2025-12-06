package game.core;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import game.entities.food.Food;
import game.entities.hazard.Hazard;
import game.entities.penguin.Penguin;
import game.enums.Direction;
import game.utils.Position;

public class IcyTerrain {
    private ArrayList<ArrayList<ArrayList<ITerrainObject>>> grid; // 10x10 gridwhere each cell contains a list
    private ArrayList<Penguin> penguins; // Maintains penguin references
    private ArrayList<Food> foodItems; // Track remaining food
    private ArrayList<Hazard> hazards; // Track hazards
    private Penguin playerPenguin; // Reference to human-controlled penguin
    private Scanner scanner; // For user input
    private int currentTurn; // 1-4
    private Random random; // Centralized randomness
    

    // Initialization methods
    private void initializeGrid(){}
    private void generatePenguins(){}
    private void generateHazards(){}
    private void generateFood(){}
    private Position getRandomEdgePosition(){}
    private Position getRandomEmptyPosition(){}
    // Game loop methods
    public void startGame(){}
    private void playTurn(Penguin penguin){}
    private void handlePlayerTurn(Penguin penguin){}
    private void handleAITurn(Penguin penguin){}
    // Core movement logic - THE HEART OF THE POLYMORPHISM
    public void processPenguinSlide(Penguin penguin, Direction dir){}
    public void processHazardSlide(Hazard hazard, Direction dir){}
    private ITerrainObject detectCollision(Position start, Direction dir,ITerrainObject movingObj){}
    private void handleCollision(ITerrainObject moving, ITerrainObject stationary, Direction dir){}
    // Grid management
    public ArrayList<ITerrainObject> getObjectsAt(Position pos){}
    public void addObjectAt(Position pos, ITerrainObject obj){}
    public void removeObjectAt(Position pos, ITerrainObject obj){}
    public boolean isPositionValid(Position pos){}
    public boolean isPositionEmpty(Position pos){}
    // Display methods
    public void displayGrid(){}
    private void displayScoreboard(){}
}