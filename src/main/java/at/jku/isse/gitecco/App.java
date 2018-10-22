package at.jku.isse.gitecco;

import at.jku.isse.gitecco.cdt.CDTHelper;
import at.jku.isse.gitecco.cdt.FeatureParser;
import at.jku.isse.gitecco.cdt.TreeFeature;
import at.jku.isse.gitecco.ecco.EccoCommand;
import at.jku.isse.gitecco.ecco.EccoCommit;
import at.jku.isse.gitecco.git.*;
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

    /**
     * Main method.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {

        final List<EccoCommand> commands = new ArrayList<EccoCommand>();
        final GitHelper gitHelper = new GitHelper("C:\\obermanndavid\\git-to-ecco\\test_repo");
        final GitCommitList commits = new GitCommitList();
        String code = "";
        Change[] changes;
        int nrCommits = 0;

        //just to make sure it works
        gitHelper.checkOutCommit("master");

        //Test for Listeners
        commits.addGitBranchListener(x -> System.out.println(x.getType()));
        commits.addGitMergeListener(x -> System.out.println(x.getType()));
        commits.addGitCommitListener(x -> System.out.println(x.getType()));

        gitHelper.getAllCommits(commits);

        /*for (int i = 0; i < nrCommits; i++) { //i < commits.length-1
            code = "";

            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++"
                    +"+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Commit: "+commits.get(i)+" - "+commits.get(i+1));

            gitHelper.checkOutCommit(commits.get(i+1).getCommitName());

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

                changes = gitHelper.getFileDiffs(commits.get(i), commits.get(i+1));

                //print changes
                System.out.print("Changes at:");
                for (Change change : changes) {
                    System.out.print(change.toString()+"; ");
                }
                System.out.println();

                featureTree.linkChanges(changes);

                final List<TreeFeature> featuresToCommit = featureTree.getChangedAsList();
                final List<TreeFeature> featuresToDelete = featureTree.getToDelete();

                final FeaturePreprocessor fpp = new FeaturePreprocessor();


                fpp.preprocess(featureTree, "<filepath>");

                commands.add(new EccoCommit(featuresToCommit));            }

        }*/

        int i = 0;
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++"
                +"+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        for (EccoCommand command : commands) {
            System.out.println(i++ + " " + command.getCommandMsg());
        }

        gitHelper.checkOutCommit("master");

    }

}
