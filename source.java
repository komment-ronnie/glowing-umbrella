package com.thealgorithms.backtracking;
import java.util.*;

/**
 * Is designed to solve a chess-like problem where a knight moves around a grid of
 * cells, filling in empty cells with numbers from 1 to total without breaking any
 * constraints. The class uses a recursive algorithm to solve the problem and provides
 * a method to print the solution as a 2D array.
 */
public class KnightsTour {
    /**
     * Calculates the number of elements in a collection by iterating through linked
     * nodes, incrementing a counter until it reaches the maximum integer value or until
     * all nodes are traversed. It handles the case where the count exceeds the maximum
     * integer value and returns the calculated size.
     * 
     * @returns the number of non-null elements in the list.
     * 
     * The output is an integer value representing the count of nodes in the list. The
     * count is limited to `Integer.MAX_VALUE`, beyond which no more elements can be
     * counted. If the count exceeds this limit, it breaks out of the loop and returns
     * the current count.
     */
    public int size() {
        restartFromHead: for (;;) {
            int count = 0;
            for (Node<E> p = first(); p != null;) {
                if (p.item != null)
                    if (++count == Integer.MAX_VALUE)
                        break;  // @see Collection.size()
                if (p == (p = p.next))
                    continue restartFromHead;
            }
            return count;
        }
    }
    
    private static final int base = 12;
    private static final int[][] moves = {
        {1, -2},
        {2, -1},
        {2, 1},
        {1, 2},
        {-1, 2},
        {-2, 1},
        {-2, -1},
        {-1, -2},
    }; // Possible moves by knight on chess
    
    private static int[][] grid; // chess grid
    private static int total; // total squares in chess

    /**
     * Initializes a grid and solves a Sudoku puzzle with a given size (`base`). It
     * randomly places a number on the grid, attempts to solve it using a recursive
     * algorithm, and prints the result if successful or displays "no result" otherwise.
     * 
     * @param args command-line arguments passed to the program, which are not used in
     * this code snippet.
     * 
     * Array of type `String`, representing command-line arguments. Its size is represented
     * by `args.length`.
     */
    public static void main(String[] args) {
        grid = new int[base][base];
        total = (base - 4) * (base - 4);

        for (int r = 0; r < base; r++) {
            for (int c = 0; c < base; c++) {
                if (r < 2 || r > base - 3 || c < 2 || c > base - 3) {
                    grid[r][c] = -1;
                }
            }
        }

        int row = 2 + (int) (Math.random() * (base - 4));
        int col = 2 + (int) (Math.random() * (base - 4));

        grid[row][col] = 1;

        if (solve(row, col, 2)) {
            printResult();
        } else {
            System.out.println("no result");
        }
    }
    
    /**
     * Recursively searches for a solution to fill an m × n grid with unique numbers from
     * 1 to total. It starts by checking if the current count exceeds the total, and then
     * iterates over neighboring cells, assigning values and calling itself until it
     * detects an orphan cell or exhausts all possibilities.
     * 
     * @param row 0-based row index of the current cell being processed and is updated
     * accordingly during the recursive search process.
     * 
     * @param column 2D array index for the current cell being processed, along with the
     * corresponding `row`, to facilitate updating and backtracking through the grid
     * during the solving process.
     * 
     * @param count current number being placed on the grid, with its value incrementing
     * for each recursive call and used to determine whether the placement is valid or not.
     * 
     * @returns a boolean indicating whether a solution is found.
     */
    private static boolean solve(int row, int column, int count) {
        if (count > total) {
            return true;
        }

        List<int[]> neighbor = neighbors(row, column);

        if (neighbor.isEmpty() && count != total) {
            return false;
        }

        neighbor.sort(Comparator.comparingInt(a -> a[2]));

        for (int[] nb : neighbor) {
            row = nb[0];
            column = nb[1];
            grid[row][column] = count;
            if (!orphanDetected(count, row, column) && solve(row, column, count + 1)) {
                return true;
            }
            grid[row][column] = 0;
        }

        return false;
    }

    /**
     * Iterates through a list of moves and generates all neighboring cells for each move
     * within the grid. It checks if the neighboring cell is empty, counts the number of
     * neighbors with value 0, and adds it to a list along with the coordinates of the neighbor.
     * 
     * @param row 0-based index of the row in a grid for which the neighboring cells are
     * to be found and processed.
     * 
     * @param column 0-based index of the column in the grid where the neighboring cells
     * are to be checked for the presence of unvisited cells.
     * 
     * @returns a list of integer arrays representing neighboring cells with their counts.
     * 
     * Returns a list of arrays containing three integers, representing coordinates and
     * a count of neighboring cells with value 0 in a grid. The first two integers denote
     * the row and column indices of a cell, while the third integer represents the number
     * of neighboring cells with value 0.
     */
    private static List<int[]> neighbors(int row, int column) {
        List<int[]> neighbour = new ArrayList<>();

        for (int[] m : moves) {
            int x = m[0];
            int y = m[1];
            if (grid[row + y][column + x] == 0) {
                int num = countNeighbors(row + y, column + x);
                neighbour.add(new int[] {row + y, column + x, num});
            }
        }
        return neighbour;
    }

    /**
     * Calculates the number of neighboring cells with value 0 in a given grid at a
     * specified row and column based on predefined moves. It iterates through the moves
     * array, checks for adjacent cells with value 0, and increments the count accordingly.
     * 
     * @param row 1D index of a cell in the grid for which the number of live neighbors
     * is to be counted.
     * 
     * @param column 0-indexed column position of an element in the grid that is being
     * checked for neighboring cells.
     * 
     * @returns an integer representing the number of adjacent zeros.
     */
    private static int countNeighbors(int row, int column) {
        int num = 0;
        for (int[] m : moves) {
            if (grid[row + m[1]][column + m[0]] == 0) {
                num++;
            }
        }
        return num;
    }

    /**
     * Checks for the presence of an orphan cell in a grid by examining neighboring cells
     * and their live neighbor count. It returns true if at least one cell with no live
     * neighbors is found, indicating an orphan; otherwise, it returns false.
     * 
     * @param count number of cells with no neighbors in the current row and column that
     * have been counted so far, used to determine if an orphan has been detected.
     * 
     * @param row 2D grid's row index, used to retrieve neighboring cells for the cell
     * at the specified position and check if any of them are orphaned.
     * 
     * @param column 1-based column index of a cell being evaluated, used to retrieve its
     * neighboring cells.
     * 
     * @returns a boolean indicating whether an orphan has been detected.
     */
    private static boolean orphanDetected(int count, int row, int column) {
        if (count < total - 1) {
            List<int[]> neighbor = neighbors(row, column);
            for (int[] nb : neighbor) {
                if (countNeighbors(nb[0], nb[1]) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Iterates over a two-dimensional grid, skipping any elements with value -1, and
     * prints each non-skipped element with leading spaces to ensure alignment, followed
     * by a newline character at the end of each row.
     */
    private static void printResult() {
        for (int[] row : grid) {
            for (int i : row) {
                if (i == -1) {
                    continue;
                }
                System.out.printf("%2d ", i);
            }
            System.out.println();
        }
    }
}
