package com.samv;

import java.util.*;

/*
    This class takes printing grid assuming always ship should
    be at center of grid.
 */
public class MineFieldPrinter {
    private Map<Integer, List<Mine>> activeMines;

    public MineFieldPrinter(Map<Integer, List<Mine>> activeMines) {
        this.activeMines = activeMines;
    }


    /*
         printGrid method prints grid after executing a command/ changing direction of ship.
        It takes ship location as parameter.
    */
    public void printGrid(int shipXPosition, int shipYPosition) {

        if (activeMines.isEmpty()) {
            System.out.println(".");
        } else {
            int[] minmaxRows = calcMinMaxRowsInMatrix();
            int[] minMaxCols = calcMinMaxColsInMatrix();

            //This determines (MaxValueOfY - shipPositionY) that need to be present, so that ship/vessesl is at center
            int maxRowsAboveShip = Math.max(Math.abs(minmaxRows[0] - shipYPosition),
                    Math.abs(minmaxRows[1] - shipYPosition));

            //determines maximum columns on both sides of ship/vessel, so thay ship would be always in center
            int maxColsleftShip = Math.max(Math.abs(minMaxCols[0] - shipXPosition),
                    Math.abs(minMaxCols[1] - shipXPosition));

            // This creates new matrix of desired size, so that ship would be always be in center.
            char[][] newMatrix = new char[maxRowsAboveShip * 2 + 1][maxColsleftShip * 2 + 1];

            for (int row = 0; row < newMatrix.length; row++) {
                for (int col = 0; col < newMatrix[0].length; col++) {
                    newMatrix[row][col] = '.';
                }
            }

            // Then active mines are added to new matrix by adding logic to determine correct location of mine.
            for (int row : activeMines.keySet()) {
                List<Mine> cols = activeMines.get(row);
                for (Mine col : cols) {
                    newMatrix[row - (shipYPosition - maxRowsAboveShip)][col.getX() - (shipXPosition - maxColsleftShip)]
                            = findChar(col.getZ());
                    col.setZ(col.getZ());
                }
            }

            //this loop prints the new grid after command is executed.
            for (int row = 0; row < newMatrix.length; row++) {
                System.out.println(new String(newMatrix[row]));
            }
        }
    }

    //It calculates min and max rows that need to there in grid to
    // make all mines visible.
    private int[] calcMinMaxRowsInMatrix() {
        Set<Integer> keys = activeMines.keySet();
        TreeSet<Integer> keysSorted = new TreeSet<Integer>();
        keysSorted.addAll(keys);

        int minRow = keysSorted.first();
        int maxRow = keysSorted.last();
        return new int[]{minRow, maxRow};
    }

    //Calculates min and max columns that need to be there in a grid to
    // make all mines visible.
    private int[] calcMinMaxColsInMatrix() {

        int minColumn = 99999;
        int maxColumn = 0;

        for (int row : activeMines.keySet()) {
            List<Mine> columns = activeMines.get(row);
            for (Mine column : columns) {
                if (column.getX() < minColumn)
                    minColumn = column.getX();
                if (column.getX() > maxColumn)
                    maxColumn = column.getX();
            }
        }

        return new int[]{minColumn, maxColumn};
    }

    // Determines actual character associated with depth.
    private char findChar(int depth) {
        if (depth >= 1 && depth <= 26) {
            return (char) (depth + 96);
        } else if (depth > 27 && depth <= 52)
            return (char) (depth + 38);
        else
            return '*';
    }

}
