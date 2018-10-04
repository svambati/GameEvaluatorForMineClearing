package com.samv;

import java.util.*;

public class StarFleetGrid {
    //This Variable stores mines grid that we initialized
    private List<String> minesGrid;

    //This map stores all the location of mines where key is y values of
    //grid cuboid and stores Mine object as value
    private Map<Integer, List<Mine>> activeMines;

    //Variable to store total mines in a grid
    private int initialMinesCount;

    // Variable to store x, y, z values of ship location
    private int shipPositionX;
    private int shipPositionY;
    private int shipPositionZ;

    //This flag is turned on if  we miss any mine/mines
    private boolean checkMissed = false;


    public StarFleetGrid(List<String> minesGrid) {
        this.minesGrid = minesGrid;
        initializeGridForGame();
        activeMines = new HashMap<Integer, List<Mine>>();
        findAllMines();
        initialPositionOfShip();

    }

    /*
        this method is used to prepare grid for game.
        When we have even rows/columns then we will add
        column/row for keeping the ship at center.
     */
    private void initializeGridForGame() {
        int[] dimensionsOfGrid = findDimensionsOfGrid();
        if (dimensionsOfGrid[1] % 2 == 0) {
            for (int idx = 0; idx < this.minesGrid.size(); idx++) {
                this.minesGrid.set(idx, (this.minesGrid.get(idx) + "."));
            }
        }
        if (dimensionsOfGrid[0] % 2 == 0) {
            StringBuilder newEmptyRow = new StringBuilder();
            for (int i = 0; i < this.minesGrid.get(0).length(); i++)
                newEmptyRow.append(".");

            this.minesGrid.add(newEmptyRow.toString());
        }
    }

    //Finds dimensions of grid rows * columns * maxDepth
    private int[] findDimensionsOfGrid() {
        int rows = this.minesGrid.size();
        int columns = this.minesGrid.get(0).length();
        int maxDepth = 0;
        int depth = 0;
        for (String s : this.minesGrid) {

            for (char c : s.toCharArray()) {
                if (c != '.') {
                    depth = this.findDepthOfGrid(c);
                    maxDepth = depth > maxDepth ? depth : maxDepth;
                    break;
                }
            }

            if (depth > 0)
                break;
        }
        return new int[]{rows, columns, depth};
    }

    /*
        This method helps to convert a letter in a grid
        to distance from ship in miles.
     */
    private int findDepthOfGrid(char c) {
        int depth = 0;

        if ((int) c >= 65 && (int) c <= 90) {
            depth = (int) c - 38;
        } else if ((int) c >= 97 && (int) c <= 122)
            depth = (int) c - 96;

        return depth;
    }

    // this method determines initial position of ship.
    private void initialPositionOfShip() {
        int[] dimensionsOfGrid = findDimensionsOfGrid();

        this.shipPositionX = (dimensionsOfGrid[1] / 2) + (dimensionsOfGrid[1] % 2) - 1;
        this.shipPositionY = (dimensionsOfGrid[0] / 2) + (dimensionsOfGrid[0] % 2) - 1;
        this.shipPositionZ = dimensionsOfGrid[2];

    }

    //this method finds all mines in a grid and stores corresponding values in Map.
    private void findAllMines() {
        for (int y = 0; y < this.minesGrid.size(); y++) {
            for (int x = 0; x < this.minesGrid.get(y).length(); x++) {
                if (this.minesGrid.get(y).charAt(x) != '.') {
                    int z = findDepthOfGrid(this.minesGrid.get(y).charAt(x));
                    if (activeMines.containsKey(y)) {
                        List<Mine> minesX = activeMines.get(y);
                        minesX.add(new Mine(x, y, z));
                    } else {
                        List<Mine> newXList = new ArrayList<Mine>();
                        newXList.add(new Mine(x, y, z));
                        activeMines.put(y, newXList);
                    }
                    this.initialMinesCount++;
                }
            }
        }
    }

    public List<String> getMinesGrid() {
        return minesGrid;
    }

    //This method is used to destroy mines when fire is executed.
    private boolean destroyMine(Mine mine) {

        if (mine.getX() < 0 || mine.getY() < 0)
            return false;

        boolean mineIsAboveShip = false;

        if (activeMines.containsKey(mine.getY())) {
            List<Mine> minesInRow = activeMines.get(mine.getY());
            if (minesInRow.size() == 1 && minesInRow.get(0).getX() == mine.getX()) {
                if (minesInRow.get(0).getZ() <= shipPositionZ)
                    activeMines.remove(mine.getY());
                else {
                    // if the ship is below the depth of cubiod then mine can not be destroyed.
                    //so, we are setting the z value to some value and which is interpreted programatically
                    //to '*'
                    mineIsAboveShip = true;
                    minesInRow.get(0).setZ(1000);
                }
            } else {
                for (Mine mineX : minesInRow) {
                    if (mineX.getX() == mine.getX()) {
                        if (mineX.getZ() <= shipPositionZ)
                            minesInRow.remove(mineX);
                        else {
                            mineIsAboveShip = true;
                            mineX.setZ(1000);
                        }
                    }
                }
            }

            if (mineIsAboveShip) {
                this.checkMissed = true;
                return false;
            }
            return true;
        }
        return false;
    }

    private void fireAlphaCommand(int shipPosX, int shipPosY) {
        destroyMine(new Mine(shipPosX - 1, shipPosY - 1, shipPositionZ));
        destroyMine(new Mine(shipPosX - 1, shipPosY + 1, shipPositionZ));
        destroyMine(new Mine(shipPosX + 1, shipPosY - 1, shipPositionZ));
        destroyMine(new Mine(shipPosX + 1, shipPosY + 1, shipPositionZ));
    }

    private void fireGammaCommand(int shipPosX, int shipPosY) {
        destroyMine(new Mine(shipPosX - 1, shipPosY, shipPositionZ));
        destroyMine(new Mine(shipPosX, shipPosY, shipPositionZ));
        destroyMine(new Mine(shipPosX + 1, shipPosY, shipPositionZ));
    }

    private void fireDeltaCommand(int shipPosX, int shipPosY) {

        destroyMine(new Mine(shipPosX, shipPosY - 1, shipPositionZ));
        destroyMine(new Mine(shipPosX, shipPosY, shipPositionZ));
        destroyMine(new Mine(shipPosX, shipPosY + 1, shipPositionZ));
    }

    private void fireBetaCommand(int shipPosX, int shipPosY) {
        destroyMine(new Mine(shipPosX - 1, shipPosY, shipPositionZ));
        destroyMine(new Mine(shipPosX, shipPosY - 1, shipPositionZ));
        destroyMine(new Mine(shipPosX, shipPosY + 1, shipPositionZ));
        destroyMine(new Mine(shipPosX + 1, shipPosY, shipPositionZ));
    }

    private void executeCommand(String fireCommand) {
        if (fireCommand.compareToIgnoreCase("alpha") == 0)
            fireAlphaCommand(shipPositionX, shipPositionY);
        else if (fireCommand.compareToIgnoreCase("beta") == 0)
            fireBetaCommand(shipPositionX, shipPositionY);
        else if (fireCommand.compareToIgnoreCase("gamma") == 0)
            fireGammaCommand(shipPositionX, shipPositionY);
        else if (fireCommand.compareToIgnoreCase("delta") == 0)
            fireDeltaCommand(shipPositionX, shipPositionY);
    }

    private void moveShipTo(String direction) {
        if (direction.compareToIgnoreCase("north") == 0)
            this.shipPositionY -= 1;
        else if (direction.compareToIgnoreCase("south") == 0)
            this.shipPositionY += 1;
        else if (direction.compareToIgnoreCase("east") == 0)
            this.shipPositionX += 1;
        else if (direction.compareToIgnoreCase("west") == 0)
            this.shipPositionX -= 1;
    }

    public void runScript(List<String> instructions) {
        boolean success = true;
        int instructionsStepsLeft = instructions.size();
        MineFieldPrinter gridPrinter = new MineFieldPrinter(activeMines);
        int step = 1;
        int points = 10 * initialMinesCount;
        int maxFiringPointsDeductedLimit = 5 * initialMinesCount;
        int firingPointsDeducted = 0;
        int movementPointsDeducted = 0;
        int maxPointsMovementDeductedLimit = 3 * initialMinesCount;

        while (!activeMines.isEmpty() && step <= instructions.size() && success) {
            if (checkMissed) {
                success = false;
                break;
            }

            System.out.println("Step " + step + "\n");

            gridPrinter.printGrid(shipPositionX, shipPositionY);
            String instruction = instructions.get(step - 1);
            System.out.println("\n" + instruction + "\n");
            String[] commands = instruction.split("\\s+");
            if (commands.length > 2)
                throw new IllegalArgumentException("Can not me more than two commands in a line");
            for (String command : commands) {
                command = command.toLowerCase().trim();
                if (command.compareToIgnoreCase("north") == 0 || command.compareToIgnoreCase("south") == 0 || command.compareToIgnoreCase("west") == 0 || command.compareToIgnoreCase("east") == 0) {
                    //Ship is coming down 1 mile for each command
                    shipPositionZ--;
                    if (movementPointsDeducted < maxPointsMovementDeductedLimit)
                        movementPointsDeducted += 2;
                    moveShipTo(command);
                    decrementDistance();
                    if (checkMissed)
                        success = false;
                } else if (command.compareToIgnoreCase("gamma") == 0 || command.compareToIgnoreCase("alpha") == 0 || command.compareToIgnoreCase("delta") == 0 || command.compareToIgnoreCase("beta") == 0) {
                    if (firingPointsDeducted < maxFiringPointsDeductedLimit)
                        firingPointsDeducted += 5;
                    executeCommand(command);
                    if (checkMissed)
                        break;
                } else
                    throw new IllegalArgumentException("Unknown command/direction");

            }

            gridPrinter.printGrid(shipPositionX, shipPositionY);

            System.out.println("");
            instructionsStepsLeft--;
            step++;
        }

        printScore(success, instructionsStepsLeft, points, (firingPointsDeducted + movementPointsDeducted));
    }

    private void decrementDistance() {
        for (int row : activeMines.keySet()) {
            List<Mine> rowOfMines = activeMines.get(row);
            for (Mine mineInCol : rowOfMines) {
                if (mineInCol.getZ() > 1)
                    mineInCol.setZ(mineInCol.getZ() - 1);
                else {
                    this.checkMissed = true;
                    mineInCol.setZ(1000);
                }
            }
        }
    }

    private void printScore(boolean success, int instructionStepsLeft, int totalPoints, int totalPointsdeducted) {

        if (instructionStepsLeft > 0 && success && activeMines.isEmpty()) {
            System.out.println("Pass (1)");
        } else if (instructionStepsLeft == 0 && success && activeMines.isEmpty()) {
            System.out.println("Pass (" + (totalPoints - totalPointsdeducted) + ")");
        } else {
            System.out.println("Fail (0)");
        }
    }

}
