package at.jku.isse.gitecco.featureid;

import at.jku.isse.gitecco.core.conditionparser.Feature;
import at.jku.isse.gitecco.core.git.GitCommitList;
import at.jku.isse.gitecco.core.git.GitHelper;
import at.jku.isse.gitecco.featureid.featuretree.visitor.GetGlobalFeaturesVisitor;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class App {

    private final static String REPO_PATH = "C:\\obermanndavid\\git-to-ecco\\test_repo5";
    private final static boolean DISPOSE = true;

    public static void main(String... args) throws Exception {
        final GitHelper gitHelper = new GitHelper(REPO_PATH);
        final GitCommitList commitList = new GitCommitList(REPO_PATH);
        final Set<Feature> globalFeatures = new HashSet<>();

        addAndConfigureObserver(commitList, globalFeatures);
        gitHelper.getAllCommits(commitList);

        writeToCsv(globalFeatures, gitHelper.getPath()+"_features.csv");
    }

    /**
     * Implements a CommitListener in which all global features a retrieved and added to a given set.
     * @param commitList
     * @param features
     */
    private static void addAndConfigureObserver(GitCommitList commitList, Set<Feature> features) {
        commitList.addGitCommitListener(
                (gc, gcl) -> {
                    final GetGlobalFeaturesVisitor visitor = new GetGlobalFeaturesVisitor();
                    gc.getTree().accept(visitor);

                    features.addAll(visitor.getGlobal());

                    //dispose tree if it is not needed -> for memory saving reasons.
                    if (DISPOSE) gc.disposeTree();
                }
        );
    }

    private static void writeToCsv(Set<Feature> features, String fileName) {
        final File csvFile = new File(fileName);
        FileWriter outputfile = null;

        try {
            //second parameter is boolean for appending --> never append
            outputfile = new FileWriter(csvFile, false);
        } catch (IOException ioe) {
            System.out.println("Error while handling the csv file output!");
        }

        // create CSVWriter object filewriter object as parameter
        //deprcated but no other way available --> it still works anyways
        @SuppressWarnings("deprecation") CSVWriter writer = new CSVWriter(outputfile, ';', CSVWriter.NO_QUOTE_CHARACTER);

        //adding header to csv
        writer.writeNext(new String[]{"FeatureName"});

        for (Feature feature : features) {
            writer.writeNext(new String[]{feature.getName()});
        }

        // closing writer connection
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
