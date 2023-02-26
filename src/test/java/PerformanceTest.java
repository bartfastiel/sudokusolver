import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class PerformanceTest {
    public static void main(String[] args) throws IOException {
        var allSudokus = getAllSudokus();
        for (int[][] sudokus : allSudokus) {
            var start = System.currentTimeMillis();
            var solution = new SudokuSolver(sudokus).solve();
            var end = System.currentTimeMillis();
            System.out.println("Solved in " + (end - start) + "ms");
        }
    }

    private static int[][][] getAllSudokus() throws IOException {
        var resource = PerformanceTest.class.getClassLoader().getResource("all_17_clue_sudokus.txt");
        var bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resource).openStream()));
        var numberOfSudokus = Integer.parseInt(bufferedReader.readLine());
        var all17ClueSudokus = new int[numberOfSudokus][9][9];
        for (var i = 0; i < numberOfSudokus; i++) {
            var line = bufferedReader.readLine();
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    all17ClueSudokus[i][j][k] = line.charAt(j * 9 + k) - '0';
                }
            }
        }
        return all17ClueSudokus;
    }
}
