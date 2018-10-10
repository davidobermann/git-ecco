package at.jku.isse.gitecco;

import at.jku.isse.gitecco.cdt.*;
import at.jku.isse.gitecco.ecco.EccoCommand;
import at.jku.isse.gitecco.ecco.EccoCommit;
import at.jku.isse.gitecco.git.Change;
import at.jku.isse.gitecco.git.GitHelper;
import at.jku.isse.gitecco.preprocessor.FeaturePreprocessor;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main app for the git to ecco tool.
 */
public class App {

    private static boolean DEBUG = false;
    private static int COMMIT_CNT = 3;

    /**
     * Main method.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {

        final List<EccoCommand> commands = new ArrayList<EccoCommand>();
        final GitHelper gitHelper = new GitHelper("C:\\obermanndavid\\git-to-ecco\\test_repo");
        final String[] commits = gitHelper.getAllCommitNames();
        String code = "";
        Change[] changes;
        int nrCommits = 0;

        gitHelper.checkOutCommit("master");

        /*Debug stuff*/
        if(DEBUG) {
            nrCommits = COMMIT_CNT;
        } else {
            nrCommits = commits.length-1;
        }

        for (int i = 0; i < nrCommits; i++) { //i < commits.length-1
            code = "";

            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++"
                    +"+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Commit: "+commits[i]+" - "+commits[i+1]);

            gitHelper.checkOutCommit(commits[i+1]);

            List<String> codelist = Files.readAllLines(Paths.get("C:\\obermanndavid\\git-to-ecco\\test_repo\\test.cpp"));
            code = codelist.stream().collect(Collectors.joining("\n"));

            if(code.length() > 1) {
                //parse file and prepare the translation unit
                final IASTTranslationUnit translationUnit = CDTHelper.parse(code.toCharArray());
                //get all the preprocessor statements
                final IASTPreprocessorStatement[] ppstatements = translationUnit.getAllPreprocessorStatements();
                final FeatureParser featureParser = new FeatureParser();

                TreeFeature featureTree = featureParser.parseToTreeDef(ppstatements, codelist.size());
                featureTree.printAll();
                System.out.println("-----------------------");

                changes = gitHelper.getFileDiffs(commits[i], commits[i+1]);

                //print changes
                System.out.print("Changes at:");
                for (Change change : changes) {
                    System.out.print(change.toString()+"; ");
                }
                System.out.println();

                featureTree.linkChanges(changes);

                final List<TreeFeature> featuresToCommit = featureTree.getChangedAsList();
                final List<TreeFeature> featuresToDelete = featureTree.getToDelete();

                /*System.out.println("-----------------------");
                //generate ecco commits
                System.out.println("Commits to make:");
                for (TreeFeature feature : featuresToCommit) {
                    System.out.println(feature.getNames());
                    if(!(feature.getFeatureType() == FeatureType.IF)) {
                        System.out.println("--exclusive");
                    }
                }
                System.out.println("-----------------------");

                System.out.println("To delete:");
                for(TreeFeature tf : featuresToDelete) {
                    System.out.println(tf.getNames());
                }*/

                final FeaturePreprocessor fpp = new FeaturePreprocessor();
                /*String newFile = fpp.getCommitFileContent(featuresToDelete.toArray(new Feature[featuresToDelete.size()]),
                        "C:\\obermanndavid\\git-to-ecco\\test_repo\\test.cpp");*/

                fpp.preprocess(featureTree, "<filepath>");

                commands.add(new EccoCommit(featuresToCommit));            }

        }

        int i = 0;
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++"
                +"+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        for (EccoCommand command : commands) {
            System.out.println(i++ + " " + command.getCommandMsg());
        }

        gitHelper.checkOutCommit("master");

    }

}
