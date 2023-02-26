import java.util.Arrays;
import java.util.stream.Collectors;

public class SudokuSolver {

    private final int[][] grid;
    private final boolean[][] usedNumbersPerRow = new boolean[9][9];
    private final boolean[][] usedNumbersPerColumn = new boolean[9][9];
    private final boolean[][] usedNumbersPerBlock = new boolean[9][9];

    public SudokuSolver(int[][] grid) {
        this.grid = grid;
        if (grid.length != 9)
            throw new IllegalArgumentException("Sudoku has to have 9 rows but had " + grid.length + " rows");
        if (Arrays.stream(grid).anyMatch(row -> row.length != 9))
            throw new IllegalArgumentException("Each row has to have 9 cells");
        if (Arrays.stream(grid).flatMapToInt(Arrays::stream).anyMatch(cell -> cell < 0 || 9 < cell))
            throw new IllegalArgumentException("Only numbers [0,9] are valid inputs");
        var numbersGiven = Arrays.stream(grid).flatMapToInt(Arrays::stream).filter(c -> c != 0).count();
        if (numbersGiven < 17)
            throw new IllegalArgumentException("With " + numbersGiven + " numbers (less then 17), there are definitely several solutions");
        for (var p = 0; p < 81; p++) {
            var row = p / 9;
            var column = p % 9;
            var block = (row / 3) * 3 + column / 3;
            var v = grid[row][column];
            if (v != 0) {
                if (usedNumbersPerRow[row][v - 1])
                    throw new IllegalArgumentException(v + " twice in row " + row);
                if (usedNumbersPerColumn[column][v - 1])
                    throw new IllegalArgumentException(v + " twice in column " + column);
                if (usedNumbersPerBlock[block][v - 1])
                    throw new IllegalArgumentException(v + " twice in block " + block);
                usedNumbersPerRow[row][v - 1] = true;
                usedNumbersPerColumn[column][v - 1] = true;
                usedNumbersPerBlock[block][v - 1] = true;
            }
        }
    }

    public int[][] solve() {
        while (insertEasyNumbers()) {
            // do nothing
        }
        var solution = tryToSolveRecursively(0);
        if (solution == null) throw new IllegalArgumentException("Sudoku is unsolvable");
        return solution;
    }

    private boolean insertEasyNumbers() {
        var changesMade = false;
        for (var p = 0; p < 81; p++) {
            var row = p / 9;
            var column = p % 9;
            var block = (row / 3) * 3 + column / 3;
            var v = grid[row][column];
            if (v == 0) {
                var impossibleNumbers = new boolean[9];
                for (var i = 0; i < 9; i++) {
                    var rowValue = grid[row][i];
                    if (rowValue != 0)
                        impossibleNumbers[rowValue - 1] = true;
                    var columnValue = grid[i][column];
                    if (columnValue != 0)
                        impossibleNumbers[columnValue - 1] = true;
                    var blockValue = grid[(row / 3) * 3 + i / 3][(column / 3) * 3 + i % 3];
                    if (blockValue != 0)
                        impossibleNumbers[blockValue - 1] = true;
                }
                var possibleNumbers = 0;
                for (var i = 0; i < 9; i++) {
                    if (!impossibleNumbers[i]) {
                        possibleNumbers++;
                    }
                }
                if (possibleNumbers == 1) {
                    for (var i = 0; i < 9; i++) {
                        if (!impossibleNumbers[i]) {
                            grid[row][column] = i + 1;
                            usedNumbersPerRow[row][i] = true;
                            usedNumbersPerColumn[column][i] = true;
                            usedNumbersPerBlock[block][i] = true;
                            changesMade = true;
                            break;
                        }
                    }
                }
            }
        }
        return changesMade;
    }

    private int[][] tryToSolveRecursively(int p) {
        if (p == 81) {
            var solution = new int[9][9];
            for (var i = 0; i < 9; i++) {
                System.arraycopy(grid[i], 0, solution[i], 0, 9);
            }
            return solution;
        }
        var row = p / 9;
        var column = p % 9;
        var block = (row / 3) * 3 + column / 3;
        if (grid[row][column] == 0) {
            int[][] solution = null;
            for (var guess = 1; guess <= 9; guess++) {
                if (usedNumbersPerRow[row][guess - 1]) continue;
                if (usedNumbersPerColumn[column][guess - 1]) continue;
                if (usedNumbersPerBlock[block][guess - 1]) continue;
                grid[row][column] = guess;
                usedNumbersPerRow[row][guess - 1] = true;
                usedNumbersPerColumn[column][guess - 1] = true;
                usedNumbersPerBlock[block][guess - 1] = true;
                var newSolution = tryToSolveRecursively(p + 1);
                if (newSolution != null) {
                    if (solution != null)
                        throw new IllegalArgumentException("Sudoku has several solutions");
                    solution = newSolution;
                }
                usedNumbersPerRow[row][guess - 1] = false;
                usedNumbersPerColumn[column][guess - 1] = false;
                usedNumbersPerBlock[block][guess - 1] = false;
            }
            grid[row][column] = 0;
            return solution;
        }
        return tryToSolveRecursively(p + 1);
    }

    public String toString() {
        return toString(grid);
    }

    private String toString(int[][] grid) {
        return "Sudoku-Grid\n" + Arrays.stream(grid).map(Arrays::toString).collect(Collectors.joining("\n"));
    }
}