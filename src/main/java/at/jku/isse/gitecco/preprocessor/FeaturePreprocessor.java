package at.jku.isse.gitecco.preprocessor;

import at.jku.isse.gitecco.cdt.Feature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for pseudo preprocessing c/c++ files.
 * With the provided methods it is possible the cut the unchanged features
 */
public class FeaturePreprocessor {

    /**
     * Cuts all the unchanged features of
     *
     * @param featuresToCut the features to cut / all the unchanged features.
     * @param fileToCut     the file, which should be freed of unchanged features.
     * @return The content of the new File as String.
     * @throws IOException
     */
    public String getCommitFileContent(Feature[] featuresToCut, String fileToCut) throws IOException {
        List<String> result = Files.readAllLines(Paths.get(fileToCut));

        if(featuresToCut.length > 0) {
            result = cutLines(result, featuresToCut[0]);
            for (int i = 0; i < featuresToCut.length; i++) {
                result = cutLines(result, featuresToCut[i]);
            }
            String newFileContent = result.stream()
                    .filter(s -> !s.equals("###lineremoved###"))
                    .filter(s -> !s.contains("#if") && !s.contains("#endif") && !s.contains("#else"))
                    .collect(Collectors.joining("\n"));

            return newFileContent;
        }
        return "";
    }

    /**
     * Cuts out the features of a list of lines form a file.
     *
     * @param lines   List of lines to work with.
     * @param feature Features to be cut away from the content.
     * @return List of the lines without those belonging to given features.
     * @throws IOException
     */
    private List<String> cutLines(List<String> lines, Feature feature) throws IOException {
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

    private List<String> tryForever(String fileToCut){
        List<String> result;
        try {
            result = Files.lines(Paths.get(fileToCut)).collect(Collectors.toList());
        } catch(Exception e) {
            result = tryForever(fileToCut);
        }
        return result;
    }

}
