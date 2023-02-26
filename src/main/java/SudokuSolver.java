import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Arrays.copyOf;

public class SudokuSolver {
    private static final int SIDE_LENGTH = 9;
    private static final int NUMBER_OF_FIELDS = SIDE_LENGTH * SIDE_LENGTH;
    private final int[] grid;
    private final boolean[] guess = new boolean[NUMBER_OF_FIELDS];
    private final short[] usedNumbersPerRow = new short[SIDE_LENGTH];
    private final short[] usedNumbersPerColumn = new short[SIDE_LENGTH];
    private final short[] usedNumbersPerBlock = new short[SIDE_LENGTH];

    public SudokuSolver(int[][] grid) {
        if (grid.length != SIDE_LENGTH)
            throw new IllegalArgumentException("Sudoku has to have " + SIDE_LENGTH + " rows but had " + grid.length + " rows");
        if (Arrays.stream(grid).anyMatch(row -> row.length != SIDE_LENGTH))
            throw new IllegalArgumentException("Each row has to have " + SIDE_LENGTH + " cells");
        if (Arrays.stream(grid).flatMapToInt(Arrays::stream).anyMatch(cell -> cell < 0 || 9 < cell))
            throw new IllegalArgumentException("Only numbers [0," + SIDE_LENGTH + "] are valid inputs");
        var numbersGiven = Arrays.stream(grid).flatMapToInt(Arrays::stream).filter(c -> c != 0).count();
        if (numbersGiven < 17)
            throw new IllegalArgumentException("With " + numbersGiven + " numbers (less then 17), there are definitely several solutions");
        this.grid = new int[SIDE_LENGTH * SIDE_LENGTH];
        for (var p = 0; p < SIDE_LENGTH * SIDE_LENGTH; p++) {
            var row = p / SIDE_LENGTH;
            var column = p % SIDE_LENGTH;
            var block = (row / 3) * 3 + column / 3;
            var v = grid[row][column];
            this.grid[p] = v;
            if (v == 0) {
                guess[p] = true;
            } else {
                if ((usedNumbersPerRow[row] & (1L << v)) != 0)
                    throw new IllegalArgumentException(v + " twice in row " + row);
                if ((usedNumbersPerColumn[column] & (1L << v)) != 0)
                    throw new IllegalArgumentException(v + " twice in column " + column);
                if ((usedNumbersPerBlock[block] & (1L << v)) != 0)
                    throw new IllegalArgumentException(v + " twice in block " + block);
                usedNumbersPerRow[row] |= 1 << v;
                usedNumbersPerColumn[column] |= 1 << v;
                usedNumbersPerBlock[block] |= 1 << v;
            }
        }
    }

    public int[][] solve() {
        int[] solution = null;
        var direction = 1;

        fieldLoop:
        for (int p = 0; p <= SIDE_LENGTH * SIDE_LENGTH; p += direction) {
            if (p == SIDE_LENGTH * SIDE_LENGTH) {
                if (solution != null) throw new IllegalArgumentException("Sudoku has several solutions");
                solution = copyOf(grid, NUMBER_OF_FIELDS);
                direction = -1;
                continue;
            }
            if (p < 0) {
                break;
            }

            var row = p / SIDE_LENGTH;
            var column = p % SIDE_LENGTH;
            var block = (row / 3) * 3 + column / 3;
            if (guess[p]) {
                var v = grid[p];
                if (1 <= v) {
                    usedNumbersPerRow[row] &= ~(1 << v);
                    usedNumbersPerColumn[column] &= ~(1 << v);
                    usedNumbersPerBlock[block] &= ~(1 << v);
                }
                while (v < 9) {
                    v++;
                    if (((usedNumbersPerRow[row] | usedNumbersPerColumn[column] | usedNumbersPerBlock[block]) & (1L << v)) != 0) continue;
                    grid[p] = v;
                    usedNumbersPerRow[row] |= 1 << v;
                    usedNumbersPerColumn[column] |= 1 << v;
                    usedNumbersPerBlock[block] |= 1 << v;
                    direction = 1;
                    continue fieldLoop;
                }
                direction = -1;
                grid[p] = 0;
            }
        }
        if (solution == null) throw new IllegalArgumentException("Sudoku is unsolvable");
        return to2D(solution);
    }

    private int[][] to2D(int[] solution) {
        var result = new int[SIDE_LENGTH][SIDE_LENGTH];
        for (var p = 0; p < SIDE_LENGTH * SIDE_LENGTH; p++) {
            var row = p / SIDE_LENGTH;
            var column = p % SIDE_LENGTH;
            result[row][column] = solution[p];
        }
        return result;
    }

    public String toString() {
        return toString(grid);
    }

    private String toString(int[] grid) {
        return "Sudoku-Grid\n" + Arrays.stream(grid).mapToObj(x -> (x % 9 == 0 ? " " : "") + x).collect(Collectors.joining("\n"));
    }
}