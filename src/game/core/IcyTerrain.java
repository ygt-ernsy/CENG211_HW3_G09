package game.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import game.entities.food.Food;
import game.entities.hazard.Hazard;
import game.entities.hazard.HazardFactory;
import game.entities.hazard.HeavyIceBlock;
import game.entities.hazard.HoleInIce;
import game.entities.hazard.LightIceBlock;
import game.entities.hazard.SeaLion;
import game.entities.penguin.Penguin;
import game.entities.penguin.PenguinFactory;
import game.entities.penguin.RockhopperPenguin;
import game.entities.penguin.RoyalPenguin;
import game.enums.Direction;
import game.utils.GameConstants;
import game.utils.Position;
import game.utils.RandomGenerator;

/**
 * IcyTerrain class - the main game controller and grid manager.
 *
 * This class manages:
 * - The 10x10 game grid with all terrain objects
 * - Turn-based gameplay for 3 penguins over 4 turns
 * - Movement and collision processing
 * - Player input and AI decision-making
 * - Game display and scoreboard
 *
 * The grid uses a triple-nested ArrayList structure where each cell
 * can contain multiple ITerrainObject instances for handling complex
 * collision scenarios.
 */
public class IcyTerrain {

    // ==================== Fields ====================

    /** The 10x10 grid where each cell contains a list of terrain objects */
    private ArrayList<ArrayList<ArrayList<ITerrainObject>>> grid;

    /** List of all penguins in the game */
    private ArrayList<Penguin> penguins;

    /** List of all food items remaining on the grid */
    private ArrayList<Food> foodItems;

    /** List of all hazards on the grid */
    private ArrayList<Hazard> hazards;

    /** Reference to the human-controlled penguin */
    private Penguin playerPenguin;

    /** Scanner for reading user input */
    private Scanner scanner;

    /** Current turn number (1-4) */
    private int currentTurn;

    /** Flag to track if game is over */
    private boolean gameOver;

    // ==================== Constructor ====================

    /**
     * Creates a new IcyTerrain and initializes the game.
     * Sets up the grid, generates penguins, hazards, and food items.
     */
    public IcyTerrain() {
        this.scanner = new Scanner(System.in);
        this.penguins = new ArrayList<>();
        this.foodItems = new ArrayList<>();
        this.hazards = new ArrayList<>();
        this.currentTurn = 1;
        this.gameOver = false;

        // Initialize the game
        initializeGrid();
        generatePenguins();
        generateHazards();
        generateFood();
    }

    // ==================== Initialization Methods ====================

    /**
     * Initializes the 10x10 grid with empty cell lists.
     * Each cell is an ArrayList that can hold multiple ITerrainObjects.
     */
    private void initializeGrid() {
        grid = new ArrayList<>();
        for (int y = 0; y < GameConstants.GRID_SIZE; y++) {
            ArrayList<ArrayList<ITerrainObject>> row = new ArrayList<>();
            for (int x = 0; x < GameConstants.GRID_SIZE; x++) {
                row.add(new ArrayList<ITerrainObject>());
            }
            grid.add(row);
        }
    }

    /**
     * Generates 3 penguins and places them on random edge positions.
     * One penguin is randomly assigned to the player.
     */
    private void generatePenguins() {
        for (int i = 0; i < GameConstants.PENGUIN_COUNT; i++) {
            String name = "P" + (i + 1);
            Position pos = getRandomEdgePosition();

            // Create a random penguin type
            Penguin penguin = PenguinFactory.createRandomPenguin(name, pos);

            // Add to grid and tracking list
            addObjectAt(pos, penguin);
            penguins.add(penguin);
        }

        // Randomly assign one penguin to the player
        int playerIndex = RandomGenerator.nextInt(penguins.size());
        playerPenguin = penguins.get(playerIndex);
        playerPenguin.setPlayerControlled(true);
    }

    /**
     * Generates 15 hazards and places them on random empty positions.
     * Hazards cannot be placed on edge positions (where penguins start).
     */
    private void generateHazards() {
        for (int i = 0; i < GameConstants.HAZARD_COUNT; i++) {
            Position pos = getRandomEmptyPosition();

            // Create a random hazard type
            Hazard hazard = HazardFactory.createRandomHazard(pos);

            // Add to grid and tracking list
            addObjectAt(pos, hazard);
            hazards.add(hazard);
        }
    }

    /**
     * Generates 20 food items and places them on random empty positions.
     * Food items cannot overlap with hazards but can be on any non-occupied square.
     */
    private void generateFood() {
        for (int i = 0; i < GameConstants.FOOD_COUNT; i++) {
            Position pos = getRandomEmptyPosition();

            // Create a random food item
            Food food = new Food(pos);

            // Add to grid and tracking list
            addObjectAt(pos, food);
            foodItems.add(food);
        }
    }

    /**
     * Returns a random edge position that is not already occupied.
     * Edge positions are where penguins can start.
     * @return A random empty edge Position
     */
    private Position getRandomEdgePosition() {
        Position pos;
        do {
            pos = RandomGenerator.randomEdgePosition();
        } while (!isPositionEmpty(pos));
        return pos;
    }

    /**
     * Returns a random position (not necessarily edge) that is not occupied.
     * Used for placing hazards and food.
     * @return A random empty Position
     */
    private Position getRandomEmptyPosition() {
        Position pos;
        do {
            pos = RandomGenerator.randomPosition();
        } while (!isPositionEmpty(pos));
        return pos;
    }

    // ==================== Grid Management Methods ====================

    /**
     * Returns all objects at the specified position.
     * @param pos The position to check
     * @return ArrayList of ITerrainObjects at that position (empty list if none)
     */
    public ArrayList<ITerrainObject> getObjectsAt(Position pos) {
        if (!isPositionValid(pos)) {
            return new ArrayList<>();
        }
        return grid.get(pos.getY()).get(pos.getX());
    }

    /**
     * Adds an object to the specified position on the grid.
     * @param pos The position to add the object
     * @param obj The object to add
     */
    public void addObjectAt(Position pos, ITerrainObject obj) {
        if (isPositionValid(pos) && obj != null) {
            grid.get(pos.getY()).get(pos.getX()).add(obj);
        }
    }

    /**
     * Removes an object from the specified position on the grid.
     * @param pos The position to remove from
     * @param obj The object to remove
     */
    public void removeObjectAt(Position pos, ITerrainObject obj) {
        if (isPositionValid(pos) && obj != null) {
            grid.get(pos.getY()).get(pos.getX()).remove(obj);
        }
    }

    /**
     * Checks if a position is within the grid bounds.
     * @param pos The position to check
     * @return true if the position is valid (0-9 for both x and y)
     */
    public boolean isPositionValid(Position pos) {
        return pos != null &&
                pos.getX() >= 0 && pos.getX() < GameConstants.GRID_SIZE &&
                pos.getY() >= 0 && pos.getY() < GameConstants.GRID_SIZE;
    }

    /**
     * Checks if a position is empty (no objects at that position).
     * @param pos The position to check
     * @return true if the position is valid and has no objects
     */
    public boolean isPositionEmpty(Position pos) {
        if (!isPositionValid(pos)) {
            return false;
        }
        return getObjectsAt(pos).isEmpty();
    }

    /**
     * Removes a penguin from the game (fell into water or hole).
     * @param penguin The penguin to remove
     */
    public void removePenguinFromGame(Penguin penguin) {
        if (penguin != null) {
            removeObjectAt(penguin.getPosition(), penguin);
            penguin.setFallen(true);
        }
    }

    // ==================== Game Loop Methods ====================

    /**
     * Starts and runs the main game loop.
     * Displays initial state, runs 4 turns for each penguin, then shows results.
     */
    public void startGame() {
        // Display initial grid
        System.out.println("The initial icy terrain grid:");
        displayGrid();

        // Display penguin information
        displayPenguinInfo();

        // Run 4 turns
        for (currentTurn = 1; currentTurn <= GameConstants.TOTAL_TURNS; currentTurn++) {
            // Each penguin takes a turn in order (P1, P2, P3)
            for (Penguin penguin : penguins) {
                if (!penguin.hasFallen()) {
                    playTurn(penguin);
                }
            }
        }

        // Game over - display results
        displayGameOver();
    }

    /**
     * Displays information about all penguins at the start of the game.
     */
    private void displayPenguinInfo() {
        System.out.println("These are the penguins on the icy terrain:");
        for (Penguin penguin : penguins) {
            String playerMarker = penguin.isPlayerControlled() ? " ---> YOUR PENGUIN" : "";
            System.out.println("- Penguin " + penguin.getName().substring(1) + " (" +
                    penguin.getName() + "): " + penguin.getType().getDisplayName() + playerMarker);
        }
        System.out.println();
    }

    /**
     * Plays a single turn for a penguin.
     * Handles stunned penguins, fallen penguins, and delegates to player/AI handlers.
     * @param penguin The penguin taking the turn
     */
    private void playTurn(Penguin penguin) {
        System.out.println("*** Turn " + currentTurn + " – " + penguin.getName() +
                (penguin.isPlayerControlled() ? " (Your Penguin):" : ":"));

        // Reset Rockhopper jump state at the start of each turn
        // This prevents jump preparation from carrying over between turns
        if (penguin instanceof RockhopperPenguin) {
            ((RockhopperPenguin) penguin).resetJump();
        }

        // Check if penguin is stunned
        if (penguin.isStunned()) {
            System.out.println(penguin.getName() + "'s turn is SKIPPED due to being stunned.");
            penguin.clearStun();
            displayGrid();
            return;
        }

        // Check if penguin has fallen
        if (penguin.hasFallen()) {
            System.out.println(penguin.getName() + " has fallen and cannot move.");
            return;
        }

        // Handle turn based on player or AI
        if (penguin.isPlayerControlled()) {
            handlePlayerTurn(penguin);
        } else {
            handleAITurn(penguin);
        }

        // Display new grid state
        System.out.println("New state of the grid:");
        displayGrid();
    }

    /**
     * Handles a player-controlled penguin's turn.
     * Prompts for special action and direction input.
     * @param penguin The player's penguin
     */
    private void handlePlayerTurn(Penguin penguin) {
        boolean useSpecial = false;
        Direction specialDirection = null;
        Direction slideDirection = null;

        // Ask about special action if not used
        if (!penguin.isSpecialActionUsed()) {
            useSpecial = promptYesNo("Will " + penguin.getName() + " use its special action? Answer with Y or N");
        }

        // RoyalPenguin is special: it steps first, THEN slides (potentially different directions)
        if (penguin instanceof RoyalPenguin) {
            if (useSpecial) {
                // Get direction for the safe step
                specialDirection = promptDirection("Which direction will " + penguin.getName() +
                        " use its special action? Answer with U (Up), D (Down), L (Left), R (Right)");
                System.out.println(penguin.getName() + " chooses to USE its special action.");
                executeSpecialAction(penguin, specialDirection);
            } else if (!penguin.isSpecialActionUsed()) {
                System.out.println(penguin.getName() + " does NOT use its special action.");
            }

            // If penguin hasn't fallen, get direction for slide
            if (!penguin.hasFallen()) {
                slideDirection = promptDirection("Which direction will " + penguin.getName() +
                        " move? Answer with U (Up), D (Down), L (Left), R (Right)");
                System.out.println(penguin.getName() + " chooses to move " + slideDirection.getDisplayName() + ".");
                penguin.resetSlideCount();
                processPenguinSlide(penguin, slideDirection);
            }
        } else {
            // For King, Emperor, Rockhopper: same direction for special and slide
            slideDirection = promptDirection("Which direction will " + penguin.getName() +
                    " move? Answer with U (Up), D (Down), L (Left), R (Right)");

            if (useSpecial) {
                System.out.println(penguin.getName() + " chooses to USE its special action.");
                executeSpecialAction(penguin, slideDirection);
            } else if (!penguin.isSpecialActionUsed()) {
                System.out.println(penguin.getName() + " does NOT use its special action.");
            }

            // Execute slide if penguin hasn't fallen
            if (!penguin.hasFallen()) {
                System.out.println(penguin.getName() + " chooses to move " + slideDirection.getDisplayName() + ".");
                penguin.resetSlideCount();
                processPenguinSlide(penguin, slideDirection);
            }
        }
    }

    /**
     * Handles an AI-controlled penguin's turn.
     * Makes decisions based on game rules for AI behavior.
     * @param penguin The AI penguin
     */
    private void handleAITurn(Penguin penguin) {
        // Determine if AI uses special action (30% chance, or automatic for Rockhopper)
        boolean useSpecial = false;
        Direction slideDirection = chooseAIDirection(penguin);

        // Only consider special action if not already used
        if (!penguin.isSpecialActionUsed()) {
            // Special case: Rockhopper automatically uses ability when heading toward hazard
            if (penguin instanceof RockhopperPenguin) {
                if (hasHazardInPath(penguin.getPosition(), slideDirection)) {
                    useSpecial = true;
                }
            } else if (RandomGenerator.chance(GameConstants.AI_SPECIAL_ACTION_CHANCE)) {
                useSpecial = true;
            }

            // Print decision about special action
            if (useSpecial) {
                System.out.println(penguin.getName() + " chooses to USE its special action.");

                // For RoyalPenguin, choose a safe direction for the step
                // (different from slide direction potentially)
                if (penguin instanceof RoyalPenguin) {
                    Direction stepDirection = chooseAIRoyalStepDirection(penguin);
                    executeSpecialAction(penguin, stepDirection);

                    // Recalculate slide direction from new position if penguin hasn't fallen
                    if (!penguin.hasFallen()) {
                        slideDirection = chooseAIDirection(penguin);
                    }
                } else {
                    executeSpecialAction(penguin, slideDirection);
                }
            } else {
                System.out.println(penguin.getName() + " does NOT use its special action.");
            }
        }

        // Execute slide if penguin hasn't fallen
        if (!penguin.hasFallen()) {
            System.out.println(penguin.getName() + " chooses to move " + slideDirection.getDisplayName() + ".");
            penguin.resetSlideCount();
            processPenguinSlide(penguin, slideDirection);
        }
    }

    /**
     * Chooses a safe direction for RoyalPenguin's step (special action).
     * Prioritizes directions that don't lead to hazards or water.
     * @param penguin The Royal Penguin
     * @return A safe direction, or random if none available
     */
    private Direction chooseAIRoyalStepDirection(Penguin penguin) {
        Position pos = penguin.getPosition();

        // Priority 1: Direction that is safe (empty, food only, or plugged hole)
        for (Direction dir : Direction.values()) {
            Position nextPos = pos.move(dir);
            if (isPositionValid(nextPos)) {
                ArrayList<ITerrainObject> objects = getObjectsAt(nextPos);
                boolean safe = objects.isEmpty() ||
                        objects.stream().allMatch(obj ->
                                obj instanceof Food ||
                                        (obj instanceof HoleInIce && ((HoleInIce) obj).isPlugged()));
                if (safe) {
                    return dir;
                }
            }
        }

        // Fallback: any valid direction that doesn't have unplugged hole
        for (Direction dir : Direction.values()) {
            Position nextPos = pos.move(dir);
            if (isPositionValid(nextPos)) {
                ArrayList<ITerrainObject> objects = getObjectsAt(nextPos);
                boolean hasUnpluggedHole = objects.stream()
                        .anyMatch(obj -> obj instanceof HoleInIce && !((HoleInIce) obj).isPlugged());
                if (!hasUnpluggedHole) {
                    return dir;
                }
            }
        }

        // Last resort: random (will fall into water or hole)
        return Direction.random();
    }

    /**
     * Executes a penguin's special action.
     * @param penguin The penguin using the action
     * @param direction The direction for the action
     */
    private void executeSpecialAction(Penguin penguin, Direction direction) {
        boolean success = penguin.useSpecialAction(direction, this);

        // Print action-specific messages
        switch (penguin.getType()) {
            case KING:
                // King penguin's ability is passive during slide
                break;
            case EMPEROR:
                // Emperor penguin's ability is passive during slide
                break;
            case ROYAL:
                if (success) {
                    System.out.println(penguin.getName() + " moves one square to the " + direction.getDisplayName() + ".");
                    // Collect any food at new position and print message
                    ArrayList<ITerrainObject> objects = new ArrayList<>(getObjectsAt(penguin.getPosition()));
                    for (ITerrainObject obj : objects) {
                        if (obj instanceof Food) {
                            Food food = (Food) obj;
                            penguin.collectFood(food);
                            removeObjectAt(penguin.getPosition(), food);
                            foodItems.remove(food);
                            System.out.println(penguin.getName() + " takes the " + food.getDisplayName() +
                                    " on the ground. (Weight=" + food.getWeight() + " units)");
                        }
                    }
                } else if (penguin.hasFallen()) {
                    // Determine if fell into water or hole
                    Position targetPos = penguin.getPosition().move(direction);
                    if (!isPositionValid(targetPos)) {
                        System.out.println(penguin.getName() + " accidentally steps into the water!");
                    } else {
                        System.out.println(penguin.getName() + " accidentally steps into the HoleInIce!");
                    }
                    System.out.println("*** " + penguin.getName() + " IS REMOVED FROM THE GAME!");
                } else {
                    // Step was blocked by a hazard or another penguin
                    Position targetPos = penguin.getPosition().move(direction);
                    ArrayList<ITerrainObject> blockingObjects = getObjectsAt(targetPos);
                    String blockerName = "an obstacle";
                    for (ITerrainObject obj : blockingObjects) {
                        if (obj instanceof Penguin) {
                            blockerName = ((Penguin) obj).getName();
                            break;
                        } else if (obj instanceof LightIceBlock) {
                            blockerName = "LightIceBlock";
                            break;
                        } else if (obj instanceof HeavyIceBlock) {
                            blockerName = "HeavyIceBlock";
                            break;
                        } else if (obj instanceof SeaLion) {
                            blockerName = "SeaLion";
                            break;
                        } else if (obj instanceof Hazard) {
                            blockerName = "a hazard";
                            break;
                        }
                    }
                    System.out.println(penguin.getName() + " cannot step there! Blocked by " + blockerName + ".");
                    System.out.println(penguin.getName() + "'s special action is wasted.");
                }
                break;
            case ROCKHOPPER:
                if (success) {
                    System.out.println(penguin.getName() + " prepares to jump over a hazard in its path.");
                }
                break;
        }
    }

    /**
     * Chooses the best direction for an AI penguin.
     * Priority: food > hazard (except hole) > water
     * @param penguin The AI penguin
     * @return The chosen direction
     */
    private Direction chooseAIDirection(Penguin penguin) {
        Position pos = penguin.getPosition();

        // Priority 1: Direction with food
        for (Direction dir : Direction.values()) {
            if (hasFoodInPath(pos, dir)) {
                return dir;
            }
        }

        // Priority 2: Direction with hazard (except HoleInIce)
        for (Direction dir : Direction.values()) {
            if (hasNonHoleHazardInPath(pos, dir)) {
                return dir;
            }
        }

        // Priority 3: Direction that doesn't lead immediately to water
        for (Direction dir : Direction.values()) {
            Position next = pos.move(dir);
            if (isPositionValid(next)) {
                return dir;
            }
        }

        // Fallback: any direction (will fall into water)
        return Direction.random();
    }

    /**
     * Checks if there is food in the path from a position in a direction.
     * @param start Starting position
     * @param dir Direction to check
     * @return true if food is in the path
     */
    private boolean hasFoodInPath(Position start, Direction dir) {
        Position current = new Position(start);
        while (true) {
            current = current.move(dir);
            if (!isPositionValid(current)) {
                return false;
            }

            for (ITerrainObject obj : getObjectsAt(current)) {
                if (obj instanceof Food) {
                    return true;
                }
                // Stop at any non-food object
                if (!(obj instanceof Food)) {
                    return false;
                }
            }
        }
    }

    /**
     * Checks if there is a hazard (not HoleInIce) in the path.
     * @param start Starting position
     * @param dir Direction to check
     * @return true if a non-hole hazard is in the path
     */
    private boolean hasNonHoleHazardInPath(Position start, Direction dir) {
        Position current = new Position(start);
        while (true) {
            current = current.move(dir);
            if (!isPositionValid(current)) {
                return false;
            }

            for (ITerrainObject obj : getObjectsAt(current)) {
                if (obj instanceof Hazard && !(obj instanceof HoleInIce)) {
                    return true;
                }
                if (obj instanceof HoleInIce && !((HoleInIce) obj).isPlugged()) {
                    return false; // Avoid unplugged holes
                }
            }
        }
    }

    /**
     * Checks if there is any hazard in the path.
     * @param start Starting position
     * @param dir Direction to check
     * @return true if any hazard is in the path
     */
    private boolean hasHazardInPath(Position start, Direction dir) {
        Position current = new Position(start);
        while (true) {
            current = current.move(dir);
            if (!isPositionValid(current)) {
                return false;
            }

            for (ITerrainObject obj : getObjectsAt(current)) {
                if (obj instanceof Hazard) {
                    // Skip plugged holes
                    if (obj instanceof HoleInIce && ((HoleInIce) obj).isPlugged()) {
                        continue;
                    }
                    return true;
                }
            }
        }
    }

    // ==================== Movement Processing Methods ====================

    /**
     * Processes a penguin's slide in the given direction.
     * The penguin slides until hitting something or falling off the grid.
     * @param penguin The penguin sliding
     * @param dir The direction of movement
     */
    public void processPenguinSlide(Penguin penguin, Direction dir) {
        if (penguin.hasFallen()) {
            return;
        }

        penguin.setLastMoveDirection(dir);
        Position currentPos = penguin.getPosition();

        // Remove penguin from current position
        removeObjectAt(currentPos, penguin);

        // Slide step by step
        while (true) {
            Position nextPos = currentPos.move(dir);

            // Check if falling off the grid (into water)
            if (!isPositionValid(nextPos)) {
                penguin.setFallen(true);
                System.out.println(penguin.getName() + " falls into the water!");
                System.out.println("*** " + penguin.getName() + " IS REMOVED FROM THE GAME!");
                return;
            }

            // Check for Rockhopper jump
            if (penguin instanceof RockhopperPenguin) {
                RockhopperPenguin rockhopper = (RockhopperPenguin) penguin;
                if (rockhopper.shouldJumpAt(nextPos)) {
                    // Check if landing position (one more square) is valid and empty
                    Position landingPos = nextPos.move(dir);
                    if (!isPositionValid(landingPos)) {
                        // Jump fails - fall into water
                        penguin.setFallen(true);
                        System.out.println(penguin.getName() + " jumps but falls into the water!");
                        System.out.println("*** " + penguin.getName() + " IS REMOVED FROM THE GAME!");
                        rockhopper.resetJump();
                        return;
                    }

                    ArrayList<ITerrainObject> landingObjects = getObjectsAt(landingPos);
                    boolean canLand = landingObjects.isEmpty() ||
                            landingObjects.stream().allMatch(obj -> obj instanceof Food);

                    if (canLand) {
                        // Successful jump
                        System.out.println(penguin.getName() + " jumps over " +
                                getHazardNameAt(nextPos) + " in its path.");
                        currentPos = landingPos;

                        // Collect any food at landing position
                        for (ITerrainObject obj : new ArrayList<>(landingObjects)) {
                            if (obj instanceof Food) {
                                Food food = (Food) obj;
                                penguin.collectFood(food);
                                removeObjectAt(landingPos, food);
                                foodItems.remove(food);
                                System.out.println(penguin.getName() + " takes the " + food.getDisplayName() +
                                        " on the ground. (Weight=" + food.getWeight() + " units)");
                                // Stop at food
                                penguin.setPosition(currentPos);
                                addObjectAt(currentPos, penguin);
                                rockhopper.resetJump();
                                return;
                            }
                        }

                        rockhopper.resetJump();
                        continue; // Continue sliding from landing position
                    } else {
                        // Jump fails - collide with hazard
                        System.out.println(penguin.getName() + " tries to jump but the landing spot is occupied!");
                        // Fall through to normal collision handling
                    }
                }
            }

            // Check for voluntary stop (King/Emperor ability)
            if (penguin.shouldStopHere(nextPos)) {
                currentPos = nextPos;
                penguin.setPosition(currentPos);
                addObjectAt(currentPos, penguin);
                System.out.println(penguin.getName() + " stops at an empty square using its special action.");
                return;
            }

            // Check for objects at next position
            ArrayList<ITerrainObject> objectsAtNext = getObjectsAt(nextPos);

            if (!objectsAtNext.isEmpty()) {
                // Check for food first (penguin stops and collects)
                for (ITerrainObject obj : new ArrayList<>(objectsAtNext)) {
                    if (obj instanceof Food) {
                        Food food = (Food) obj;
                        currentPos = nextPos;
                        penguin.setPosition(currentPos);
                        addObjectAt(currentPos, penguin);
                        penguin.collectFood(food);
                        removeObjectAt(nextPos, food);
                        foodItems.remove(food);
                        System.out.println(penguin.getName() + " takes the " + food.getDisplayName() +
                                " on the ground. (Weight=" + food.getWeight() + " units)");
                        return;
                    }
                }

                // Check for other objects (penguin, hazard)
                for (ITerrainObject obj : objectsAtNext) {
                    if (obj instanceof Penguin) {
                        // Collision with another penguin - movement transfer
                        penguin.setPosition(currentPos);
                        addObjectAt(currentPos, penguin);
                        Penguin otherPenguin = (Penguin) obj;
                        System.out.println(penguin.getName() + " collides with " + otherPenguin.getName() + "!");
                        System.out.println(otherPenguin.getName() + " starts sliding " + dir.getDisplayName() + ".");
                        processPenguinSlide(otherPenguin, dir);
                        return;
                    }

                    if (obj instanceof Hazard) {
                        handlePenguinHazardCollision(penguin, (Hazard) obj, currentPos, dir);
                        return;
                    }
                }
            }

            // Move to next position
            currentPos = nextPos;
        }
    }

    /**
     * Handles collision between a penguin and a hazard.
     * @param penguin The penguin that collided
     * @param hazard The hazard hit
     * @param stopPos The position where penguin was before collision
     * @param dir The direction of movement
     */
    private void handlePenguinHazardCollision(Penguin penguin, Hazard hazard, Position stopPos, Direction dir) {
        if (hazard instanceof HoleInIce) {
            HoleInIce hole = (HoleInIce) hazard;
            if (hole.isPlugged()) {
                // Pass over plugged hole
                Position nextPos = stopPos.move(dir);
                // Continue sliding from hole position
                penguin.setPosition(nextPos);
                addObjectAt(nextPos, penguin);
                removeObjectAt(nextPos, penguin);
                processPenguinSlide(penguin, dir);
            } else {
                // Fall into hole
                penguin.setFallen(true);
                System.out.println(penguin.getName() + " falls into the HoleInIce!");
                System.out.println("*** " + penguin.getName() + " IS REMOVED FROM THE GAME!");
            }
        } else if (hazard instanceof LightIceBlock) {
            // Penguin stops and is stunned, ice block slides
            penguin.setPosition(stopPos);
            addObjectAt(stopPos, penguin);
            penguin.setStunned(true);
            System.out.println(penguin.getName() + " hits a LightIceBlock and is STUNNED!");
            System.out.println("LightIceBlock starts sliding " + dir.getDisplayName() + ".");
            processHazardSlide((LightIceBlock) hazard, dir);
        } else if (hazard instanceof HeavyIceBlock) {
            // Penguin stops and drops lightest food
            penguin.setPosition(stopPos);
            addObjectAt(stopPos, penguin);
            Food droppedFood = penguin.dropLightestFood();
            System.out.println(penguin.getName() + " hits a HeavyIceBlock!");
            if (droppedFood != null) {
                System.out.println(penguin.getName() + " drops " + droppedFood.getDisplayName() +
                        " (" + droppedFood.getWeight() + " units) as a penalty.");
            }
        } else if (hazard instanceof SeaLion) {
            // Penguin bounces back, sea lion slides forward
            System.out.println(penguin.getName() + " hits a SeaLion and bounces back!");
            Direction bounceDir = dir.getOpposite();

            // Sea lion slides in original direction
            System.out.println("SeaLion starts sliding " + dir.getDisplayName() + ".");
            processHazardSlide((SeaLion) hazard, dir);

            // Penguin bounces in opposite direction
            penguin.setPosition(stopPos);
            addObjectAt(stopPos, penguin);
            System.out.println(penguin.getName() + " slides " + bounceDir.getDisplayName() + ".");
            removeObjectAt(stopPos, penguin);
            processPenguinSlide(penguin, bounceDir);
        }
    }

    /**
     * Processes a hazard's slide in the given direction.
     * @param hazard The hazard sliding
     * @param dir The direction of movement
     */
    public void processHazardSlide(Hazard hazard, Direction dir) {
        Position currentPos = hazard.getPosition();

        // Remove hazard from current position
        removeObjectAt(currentPos, hazard);

        // Slide step by step
        while (true) {
            Position nextPos = currentPos.move(dir);

            // Check if falling off the grid
            if (!isPositionValid(nextPos)) {
                hazards.remove(hazard);
                System.out.println(hazard.getDisplaySymbol() + " falls into the water!");
                return;
            }

            // Check for objects at next position
            ArrayList<ITerrainObject> objectsAtNext = getObjectsAt(nextPos);

            if (!objectsAtNext.isEmpty()) {
                for (ITerrainObject obj : new ArrayList<>(objectsAtNext)) {
                    // Hazard destroys food
                    if (obj instanceof Food) {
                        Food food = (Food) obj;
                        removeObjectAt(nextPos, food);
                        foodItems.remove(food);
                        System.out.println(hazard.getDisplaySymbol() + " destroys " + food.getDisplayName() + "!");
                        currentPos = nextPos;
                        continue;
                    }

                    // Hazard hits penguin - hazard stops, penguin unaffected
                    if (obj instanceof Penguin) {
                        hazard.setPosition(currentPos);
                        addObjectAt(currentPos, hazard);
                        System.out.println(hazard.getDisplaySymbol() + " stops near " +
                                ((Penguin) obj).getName() + ".");
                        return;
                    }

                    // Hazard hits HoleInIce
                    if (obj instanceof HoleInIce) {
                        HoleInIce hole = (HoleInIce) obj;
                        if (hole.isPlugged()) {
                            // Pass over plugged hole
                            currentPos = nextPos;
                            continue;
                        } else {
                            // Fall into hole and plug it
                            hole.setPlugged(true);
                            hazards.remove(hazard);
                            System.out.println(hazard.getDisplaySymbol() + " falls into HoleInIce and PLUGS it!");
                            return;
                        }
                    }

                    // LightIceBlock hits SeaLion - transfer movement
                    if (hazard instanceof LightIceBlock && obj instanceof SeaLion) {
                        hazard.setPosition(currentPos);
                        addObjectAt(currentPos, hazard);
                        System.out.println("LightIceBlock hits SeaLion!");
                        System.out.println("SeaLion starts sliding " + dir.getDisplayName() + ".");
                        processHazardSlide((SeaLion) obj, dir);
                        return;
                    }

                    // Hit another hazard - stop
                    if (obj instanceof Hazard) {
                        hazard.setPosition(currentPos);
                        addObjectAt(currentPos, hazard);
                        System.out.println(hazard.getDisplaySymbol() + " stops at " +
                                ((Hazard) obj).getDisplaySymbol() + ".");
                        return;
                    }
                }
            }

            // Move to next position
            currentPos = nextPos;
        }
    }

    /**
     * Gets the name of the hazard at a position (for display).
     * @param pos The position to check
     * @return The hazard name or "hazard"
     */
    private String getHazardNameAt(Position pos) {
        for (ITerrainObject obj : getObjectsAt(pos)) {
            if (obj instanceof LightIceBlock) return "LightIceBlock";
            if (obj instanceof HeavyIceBlock) return "HeavyIceBlock";
            if (obj instanceof SeaLion) return "SeaLion";
            if (obj instanceof HoleInIce) return "HoleInIce";
        }
        return "hazard";
    }

    // ==================== Input Methods ====================

    /**
     * Prompts the user for a yes/no response.
     * @param prompt The prompt to display
     * @return true for yes, false for no
     */
    private boolean promptYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " --> ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y")) {
                return true;
            } else if (input.equals("N")) {
                return false;
            }
            System.out.println("Invalid input. Please enter Y or N.");
        }
    }

    /**
     * Prompts the user for a direction.
     * @param prompt The prompt to display
     * @return The chosen Direction
     */
    private Direction promptDirection(String prompt) {
        while (true) {
            System.out.print(prompt + " --> ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return Direction.fromString(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input. Please enter U, D, L, or R.");
            }
        }
    }

    // ==================== Display Methods ====================

    /**
     * Displays the current state of the grid.
     */
    public void displayGrid() {
        String separator = GameConstants.GRID_SEPARATOR;

        System.out.println(separator);

        for (int y = 0; y < GameConstants.GRID_SIZE; y++) {
            System.out.print("|");
            for (int x = 0; x < GameConstants.GRID_SIZE; x++) {
                ArrayList<ITerrainObject> objects = grid.get(y).get(x);
                String cellContent = "  ";

                if (!objects.isEmpty()) {
                    // Priority: Penguin > Hazard > Food
                    for (ITerrainObject obj : objects) {
                        if (obj instanceof Penguin) {
                            cellContent = obj.getDisplaySymbol();
                            break;
                        }
                    }
                    if (cellContent.equals("  ")) {
                        for (ITerrainObject obj : objects) {
                            if (obj instanceof Hazard) {
                                cellContent = obj.getDisplaySymbol();
                                break;
                            }
                        }
                    }
                    if (cellContent.equals("  ")) {
                        for (ITerrainObject obj : objects) {
                            if (obj instanceof Food) {
                                cellContent = obj.getDisplaySymbol();
                                break;
                            }
                        }
                    }
                }

                // Pad to 4 characters
                System.out.print(" " + String.format("%-2s", cellContent) + " |");
            }
            System.out.println();
            System.out.println(separator);
        }
        System.out.println();
    }

    /**
     * Displays the final scoreboard at the end of the game.
     */
    private void displayScoreboard() {
        System.out.println("***** SCOREBOARD FOR THE PENGUINS *****");

        // Sort penguins by total weight (descending)
        List<Penguin> sortedPenguins = penguins.stream()
                .sorted(Comparator.comparingInt(Penguin::calculateTotalWeight).reversed())
                .collect(Collectors.toList());

        String[] places = {"1st", "2nd", "3rd"};

        for (int i = 0; i < sortedPenguins.size(); i++) {
            Penguin penguin = sortedPenguins.get(i);
            String playerMarker = penguin.isPlayerControlled() ? " (Your Penguin)" : "";

            System.out.println("* " + places[i] + " place: " + penguin.getName() + playerMarker);
            System.out.println("  |---> Food items: " + penguin.getCollectedFoodString());
            System.out.println("  |---> Total weight: " + penguin.calculateTotalWeight() + " units");
        }
    }

    /**
     * Displays the game over message and final scoreboard.
     */
    private void displayGameOver() {
        System.out.println();
        System.out.println("***** GAME OVER *****");
        System.out.println();
        displayScoreboard();
    }
}
