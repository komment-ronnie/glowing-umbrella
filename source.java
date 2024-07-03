package com.thealgorithms.backtracking;
import java.util.*;

public class KnightsTour {
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
     * creates a grid with randomly set values, finds a row and column that are adjacent
     * to each other, sets the value of the cell at that intersection to 1, and then
     * solves for the remaining cells using a recursive algorithm.
     * 
     * @param args 0-dimensional array of command-line arguments passed to the program,
     * which is not used in the provided code.
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
     * solves a Sudoku puzzle by checking if the given row, column and count can be filled
     * with a number from 1 to total without breaking any constraints. If it can be solved,
     * the function returns true.
     * 
     * @param row 2D coordinate of a cell in the grid where the algorithm is trying to
     * find a solution for the given count.
     * 
     * @param column 2nd coordinate of the grid cell being evaluated, which is used to
     * determine the neighboring cells and sort them based on their values.
     * 
     * @param count 2D position's available neighbors to be selected for the current
     * iteration of the recursive algorithm.
     * 
     * @returns a boolean value indicating whether the Sudoku puzzle has been solved.
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
     * returns a list of adjacent cells to a given cell in a two-dimensional grid, based
     * on moves available in the grid.
     * 
     * @param row 2D co-ordinate of the cell in the grid that is being examined for neighbors.
     * 
     * @param column 1-based index of the column in the grid for which neighbors are being
     * computed.
     * 
     * @returns a list of arrays containing the neighbors of a given cell in the grid,
     * along with the number of cells adjacent to each neighbor.
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
     * counts the number of cells at a given row and column that are surrounded by cells
     * of a specific value (probable a "live" cell). It does so by iterating over all
     * possible move combinations and checking if the adjacent cells are the specified value.
     * 
     * @param row 1D coordinate of the grid cell that is being analyzed for neighbors.
     * 
     * @param column 1D position of the cell being counted as a neighbor within the grid.
     * 
     * @returns the number of unexplored cells in a given row and column of the grid.
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
     * determines whether a given cell is an orphan by iterating through its neighboring
     * cells and checking if any of them have zero count neighbors. If so, it returns
     * true; otherwise, it returns false.
     * 
     * @param count 1D array being analyzed, and is used to determine whether the current
     * element is an orphan based on the number of neighbors it has.
     * 
     * @param row 2D coordinate of the cell being evaluated for orphan status.
     * 
     * @param column 2D position of the cell in the grid, and is used to determine which
     * cells are neighbors of the cell being analyzed.
     * 
     * @returns a boolean value indicating whether an orphaned cell exists at the specified
     * row and column.
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
     * loops through a 2D array `grid` and prints each element, skipping any with value
     * `-1`.
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
