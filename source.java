package com.thealgorithms.backtracking;
import java.util.*;

/**
 * Is an implementation of the knight's tour problem, which involves finding a path
 * that visits every square of a chessboard exactly once and returns to the starting
 * position. The class provides methods for counting neighbors of a cell, detecting
 * orphaned cells, and printing each element in the grid. It also defines several
 * arrays and lists to store the grid and neighboring cells information.
 */
public class KnightsTour {
    /**
     * In Java recursively counts the number of elements in a collection, restarts from
     * the head of the collection when it reaches the maximum value, and returns the count.
     * 
     * @returns the number of nodes in the LinkedList.
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
     * Initializes a grid with random values, then finds the closest pair of cells to a
     * given row and column using a brute-force search. If a solution is found, it prints
     * the result; otherwise, it prints "no result."
     * 
     * @param args 0 or more command-line arguments passed to the program when it is run,
     * which are ignored in this function.
     * 
     * * Length: `args.length` is equal to 0 or 1.
     * * Elements: If `args.length` is greater than 0, each element in the array is a
     * single character representing the command line argument.
     * 
     * Explanation limited to 3 sentences.
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
     * Determines if a given cell in a grid can be filled with a specific number of candies
     * based on the neighbors of that cell and the already filled cells in the grid.
     * 
     * @param row 2D coordinate of a cell in the grid that is being analyzed.
     * 
     * @param column 2D coordinate of the cell in the grid that needs to be filled with
     * the given `count`.
     * 
     * @param count 2D position of a cell in the grid that is being searched for an orphan,
     * and it is used to determine whether the cell has been found as an orphan during
     * the search process.
     * 
     * @returns a boolean value indicating whether the game is solved or not.
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
     * Computes and returns a list of integer arrays representing the number of neighbors
     * of a given cell in a grid, based on the cell's row and column.
     * 
     * @param row 2D grid coordinate at which to find the neighbors of a given cell.
     * 
     * @param column 2nd dimension of the grid, indicating the position of the current
     * cell in the vertical axis.
     * 
     * @returns a list of triplets containing the row and column of a neighboring cell,
     * along with the number of cells in that neighbor.
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
     * Counts the number of neighbors of a cell in a grid that are marked as 0.
     * 
     * @param row 2D grid position at which the number of neighbors is to be counted.
     * 
     * @param column 2D position of the cell within the grid, and is used to determine
     * which cells are neighbors of the specified cell in the grid.
     * 
     * @returns the number of unvisited neighbors of a given cell in a grid.
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
     * Determines if a given cell is an orphan by checking if it has no neighbors with a
     * count of 0.
     * 
     * @param count 2D coordinate of the cell being checked for orphan status.
     * 
     * @param row 1D coordinate of a cell within the grid being analyzed for orphan status.
     * 
     * @param column 2D position of the cell in the grid, which is used to determine the
     * neighbors of the current cell and check if it is an orphan.
     * 
     * @returns a boolean value indicating whether an orphan cell exists in the given grid.
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
     * Prints a matrix represented as an array of integers, where each integer represents
     * a cell value in a grid. It iterates through each row and then each cell within
     * that row, printing the value of each cell separated by a space.
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
