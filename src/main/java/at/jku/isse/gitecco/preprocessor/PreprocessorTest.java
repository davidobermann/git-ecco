package at.jku.isse.gitecco.preprocessor;

import at.jku.isse.gitecco.cdt.Feature;
import at.jku.isse.gitecco.git.Change;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PreprocessorTest {
    public static void main(String[] args) throws IOException {
        Change[] changes = {new Change(23, 3), new Change(36, 7), new Change(45, 5)};
        Path path = Paths.get("C:\\obermanndavid\\git-to-ecco\\test_repo\\test.cpp");
        //String[] lines = Files.lines(path).toArray(String[]::new);
        List<String> result = Files.lines(path).collect(Collectors.toList());
        //result = cutLines(result, changes[0]);
        for (int i = 0; i < changes.length; i++) {
            //result = cutLines(result,changes[i]);
        }
        String newFileContent = result.stream().filter(s -> !s.equals("###lineremoved###")).collect(Collectors.joining("\n"));
        System.out.println(newFileContent);
    }

    private static String getCommitFileContent(Feature[] featuresToCut, String fileToCut) throws IOException {
        Path path = Paths.get(fileToCut);
        List<String> result = Files.lines(path).collect(Collectors.toList());
        result = cutLines(result, featuresToCut[0]);
        for (int i = 0; i < featuresToCut.length; i++) {
            result = cutLines(result, featuresToCut[i]);
        }
        String newFileContent = result.stream().filter(s -> !s.equals("###lineremoved###")).collect(Collectors.joining("\n"));
        return newFileContent;
    }

    private static List<String> cutLines(List<String> lines, Feature feature) throws IOException {
        ArrayList<String> ret = new ArrayList<>();

        int i = 1;
        for (String line : lines) {
            if (i < feature.getStartingLineNumber() || i > feature.getEndingLineNumber()) {
                ret.add(line);
            } else {
                ret.add("###lineremoved###");
            }
            i++;
        }

        return ret;
    }
}
