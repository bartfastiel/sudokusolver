import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

public class PerformanceTest {

    @Test
    public void all17ClueSudokus() throws IOException {
        runAll(PerformanceTest.class.getClassLoader().getResource("all_17_clue_sudokus.txt"));
    }

    @Test
    public void randomTests() throws IOException {
        TestSetTransformer.transform();
        var randomSets = PerformanceTest.class.getClassLoader().getResource("randomTests");
        var files = new File(randomSets.getPath()).listFiles();
        Arrays.sort(files, Comparator.comparing(File::getName));
        for (File file : files) {
            System.out.println("Running " + file.getName());
            var name = file.getName();
            if (!name.endsWith(".txt"))
                continue;
            runAll(file.toURL());
        }
    }

    private static void runAll(URL filename) throws IOException {
        var allSudokus = getAllSudokus(filename);
        var times = new HashSet<Long>();
        System.out.println("                                                                                  time in ms |  average |   min |   max | givens");
        int sampleSize = 100;
        for (int i = 0; i < allSudokus.length && i < sampleSize; i++) {
            var sudoku = allSudokus[i];
            System.out.print(Arrays.stream(sudoku).map(row -> Arrays.stream(row).mapToObj(String::valueOf).collect(Collectors.joining())).collect(Collectors.joining("")));
            var start = System.currentTimeMillis();
            try {
                new SudokuSolver(sudoku).solve();
            } catch (Exception e) {
            }
            var end = System.currentTimeMillis();
            var time = end - start;
            times.add(time);
            System.out.printf(" %10d | %8.2f | %5d | %5d | %5d%n",
                    time,
                    times.stream().mapToLong(Long::longValue).average().orElseThrow(),
                    times.stream().mapToLong(Long::longValue).min().orElseThrow(),
                    times.stream().mapToLong(Long::longValue).max().orElseThrow(),
                    Arrays.stream(sudoku).mapToInt(row -> (int) Arrays.stream(row).filter(value -> value != 0).count()).sum()
            );
        }
        System.out.println();
        System.out.println("Number of sudokus: " + times.size());
        System.out.println("Average time: " + times.stream().mapToLong(Long::longValue).average().orElseThrow());
        System.out.println("Min time: " + times.stream().mapToLong(Long::longValue).min().orElseThrow());
        System.out.println("Max time: " + times.stream().mapToLong(Long::longValue).max().orElseThrow());
    }


    private static int[][][] getAllSudokus(URL filename) throws IOException {
        var bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(filename).openStream()));
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
