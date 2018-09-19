package at.jku.isse.gitecco;

import at.jku.isse.gitecco.cdt.CDTHelper;
import at.jku.isse.gitecco.cdt.Feature;
import at.jku.isse.gitecco.cdt.FeatureParser;
import at.jku.isse.gitecco.ecco.EccoCommand;
import at.jku.isse.gitecco.ecco.EccoCommit;
import at.jku.isse.gitecco.git.Change;
import at.jku.isse.gitecco.git.GitHelper;
import at.jku.isse.gitecco.preprocessor.FeaturePreprocessor;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Main app for the git to ecco tool.
 */
public class App {
    /**
     * Main method.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {

        final String code = CDTHelper.readFile("C:\\obermanndavid\\git-to-ecco\\test_repo\\test.cpp", StandardCharsets.UTF_8);
        //parse file and prepare the translation unit
        final IASTTranslationUnit translationUnit = CDTHelper.parse(code.toCharArray());
        //get all the preprocessor statements
        final IASTPreprocessorStatement[] ppstatements = translationUnit.getAllPreprocessorStatements();
        final List<EccoCommand> commands = new ArrayList<EccoCommand>();
        Feature[] features = FeatureParser.parse(ppstatements);

        //print all features
        for (Feature f : features) {
            System.out.println(f);
        }

        /////////////////////////////////////////////////////////////////////////////////////

        final GitHelper gitHelper = new GitHelper("C:\\obermanndavid\\git-to-ecco\\test_repo");

        final String[] commits = gitHelper.getAllCommitNames();
        Change[] changes;

        for (int i = 0; i < commits.length-1; i++) {

            features = FeatureParser.parse(ppstatements);
            System.out.println("--------------------------------------");
            System.out.println("Commit: "+commits[i]+" - "+commits[i+1]);
            changes = gitHelper.getFileDiffs(commits[i], commits[i+1]);

            //print changes
            System.out.print("\nChanges at: ");
            for (Change change : changes) {
                System.out.print(change.toString()+"; ");
            }
            System.out.println();

            //link changes to features
            for (Change c : changes) {
                int j = 0;
                for (Feature f : features) {
                    if (f.checkAndAddChange(c)) {
                        System.out.println("Linked "+c.toString()+" to "+f.getName());
                        if (!((j+1 < features.length) && (features[j+1].compareTo(f) == 0))) {
                            break;
                        }
                    }
                    j++;
                }
            }

            List<Feature> featuresToCommit = new ArrayList<>();
            List<Feature> featuresToDelete = new ArrayList<>();
            //generate ecco commits
            System.out.println("\nCommits to make:");
            for (Feature f : features) {
                if (f.hasChanges()) {
                    System.out.println(" -"+f.getName());
                    featuresToCommit.add(f);
                } else {
                    featuresToDelete.add(f);
                }
            }

            final FeaturePreprocessor fpp = new FeaturePreprocessor();
            String newFile = fpp.getCommitFileContent(featuresToDelete.toArray(new Feature[featuresToDelete.size()]),
                    "C:\\obermanndavid\\git-to-ecco\\test_repo\\test.cpp");
            System.out.println(newFile);
            commands.add(
                    new EccoCommit(featuresToCommit.toArray(new Feature[featuresToCommit.size()]))
            );
        }

        System.out.println("---------------------------------------");
        for (EccoCommand command : commands) {
            System.out.println(command.getCommandMsg());
        }

    }
}
