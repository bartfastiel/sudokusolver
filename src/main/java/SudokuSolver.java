import java.util.Arrays;
import java.util.stream.Collectors;

public class SudokuSolver {

    private static final boolean DEBUG = false;
    private final int[][] grid;
    private final boolean[][] guess = new boolean[9][9];
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
            if (v == 0) {
                guess[row][column] = true;
            } else {
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
        var solution = tryToSolve();
        if (solution == null) throw new IllegalArgumentException("Sudoku is unsolvable");
        return solution;
    }

    private int[][] tryToSolve() {
        int[][] solution = null;
        var direction = 1;

        fieldLoop:
        for (int p = 0; p <= 81; p += direction) {
            if (DEBUG) System.out.println(toStringSmall(grid));
            if (p == 81) {
                if (DEBUG) System.out.println("solution found (at the end)");
                if (solution != null) throw new IllegalArgumentException("Sudoku has several solutions");
                solution = new int[9][9];
                for (var i = 0; i < 9; i++) {
                    System.arraycopy(grid[i], 0, solution[i], 0, 9);
                }
                direction = -1;
                continue;
            }
            if (DEBUG) {
                if (p > 0)
                    System.out.print(String.format("%" + (p + (p / 9)) + "s", ""));
                System.out.print("^ ");
            }
            if (p < 0) {
                System.out.println("no solution found (back at the beginning)");
                break;
            }

            var row = p / 9;
            var column = p % 9;
            var block = (row / 3) * 3 + column / 3;
            if (guess[row][column]) {
                var i = grid[row][column] - 1;
                if (0 <= i) {
                    usedNumbersPerRow[row][i] = false;
                    usedNumbersPerColumn[column][i] = false;
                    usedNumbersPerBlock[block][i] = false;
                }
                while (i < 8) {
                    i++;
                    if (usedNumbersPerRow[row][i]) continue;
                    if (usedNumbersPerColumn[column][i]) continue;
                    if (usedNumbersPerBlock[block][i]) continue;
                    grid[row][column] = i + 1;
                    if (DEBUG) System.out.println("guessing " + (i + 1));
                    usedNumbersPerRow[row][i] = true;
                    usedNumbersPerColumn[column][i] = true;
                    usedNumbersPerBlock[block][i] = true;
                    direction = 1;
                    continue fieldLoop;
                }
                if (DEBUG) System.out.println("no number found, going back");
                direction = -1;
                grid[row][column] = 0;
            } else {
                if (DEBUG) System.out.println("no guess");
            }
        }
        return solution;
    }

    public String toString() {
        return toString(grid);
    }

    private String toString(int[][] grid) {
        return "Sudoku-Grid\n" + Arrays.stream(grid).map(Arrays::toString).collect(Collectors.joining("\n"));
    }

    private String toStringSmall(int[][] grid) {
        return Arrays.stream(grid).map(r -> Arrays.stream(r).mapToObj(String::valueOf).collect(Collectors.joining(""))).collect(Collectors.joining(" "));
    }
}