import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestSetTransformer {
    public static void main(String[] args) throws IOException {
        transform();
    }

     static void transform() throws IOException {
        var randomSets = TestSetTransformer.class.getClassLoader().getResource("codewarsOutput");
        var files = new File(randomSets.getPath()).listFiles();
        assert files != null;
        for (File file : files) {
            var lines = Files.readAllLines(file.toPath());
            var out = "";
            var number = 0;
            for (int i = 0; i < lines.size(); i++) {
                var line = lines.get(i);
                if ("Input grid:".equals(line)) {
                    number++;
                    for (var j = 0; j < 9; j++) {
                        line = lines.get(++i);
                        line = line.replace(" ", "");
                        line = line.replace("[", "");
                        line = line.replace("]", "");
                        line = line.replace(",", "");
                        out += line;
                    }
                    out += "\n";
                }
            }
            out = number + "\n" + out;
            var outputFilename = "src/test/resources/randomTests/" + file.getName();
            System.out.println("Writing to " + outputFilename);
            Files.write(new File(outputFilename).toPath(), out.getBytes());
        }
    }
}
