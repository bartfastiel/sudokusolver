import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;

public class PerformanceTest {

    @Test
    public void test() throws IOException {
        var allSudokus = getAllSudokus();
        var times = new HashSet<Long>();
        System.out.println("time in ms |  average |   min |   max");
        int sampleSize = 100;
        for (int i = 0; i < allSudokus.length && i < sampleSize; i++) {
            var sudoku = allSudokus[i];
            var start = System.currentTimeMillis();
            var solution = new SudokuSolver(sudoku).solve();
            var end = System.currentTimeMillis();
            var time = end - start;
            times.add(time);
            System.out.printf("%10d | %8.2f | %5d | %5d%n",
                    time,
                    times.stream().mapToLong(Long::longValue).average().orElseThrow(),
                    times.stream().mapToLong(Long::longValue).min().orElseThrow(),
                    times.stream().mapToLong(Long::longValue).max().orElseThrow()
            );
        }
        System.out.println();
        System.out.println("Number of sudokus: " + times.size());
        System.out.println("Average time: " + times.stream().mapToLong(Long::longValue).average().orElseThrow());
        System.out.println("Min time: " + times.stream().mapToLong(Long::longValue).min().orElseThrow());
        System.out.println("Max time: " + times.stream().mapToLong(Long::longValue).max().orElseThrow());
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
