package at.jku.isse.gitecco;

import at.jku.isse.gitecco.cdt.CDTHelper;
import at.jku.isse.gitecco.cdt.Feature;
import at.jku.isse.gitecco.cdt.FeatureParser;
import at.jku.isse.gitecco.cdt.TreeFeature;
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

        gitHelper.checkOutCommit("master");

        for (int i = 0; i < commits.length-1; i++) {
            code = "";

            System.out.println("--------------------------------------");
            System.out.println("Commit: "+commits[i]+" - "+commits[i+1]);

            gitHelper.checkOutCommit(commits[i+1]);

            //cloud probably use iterator.
            //code = Files.lines(Paths.get("C:\\obermanndavid\\git-to-ecco\\test_repo\\test.cpp")).collect(Collectors.joining("\n"));
            List<String> codelist = Files.readAllLines(Paths.get("C:\\obermanndavid\\git-to-ecco\\test_repo\\test.cpp"));
            code = codelist.stream().collect(Collectors.joining("\n"));

            if(code.length() > 1) {
                //parse file and prepare the translation unit
                final IASTTranslationUnit translationUnit = CDTHelper.parse(code.toCharArray());
                //get all the preprocessor statements
                final IASTPreprocessorStatement[] ppstatements = translationUnit.getAllPreprocessorStatements();
                final FeatureParser featureParser = new FeatureParser();
                Feature[] features = featureParser.parse(ppstatements, codelist.size());

                TreeFeature newFeat = featureParser.parseToTree(ppstatements, codelist.size());
                System.out.println("++++++++++++++++++++++");
                traverse(newFeat,0);
                System.out.println("++++++++++++++++++++++");



                changes = gitHelper.getFileDiffs(commits[i], commits[i+1]);

                //print changes
                System.out.print("\nChanges at: ");
                for (Change change : changes) {
                    System.out.print(change.toString()+"; ");
                }


                /*
                //link changes to features
                for (Change c : changes) {
                    int j = 0;
                    for (Feature f : features) {
                        if (f.checkAndAddChange(c)) {
                            System.out.println("Linked "+c.toString()+" to "+f.getNames());
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
                        System.out.println(" -"+f.getNames());
                        featuresToCommit.add(f);
                    } else {
                        if(!f.isBase()) featuresToDelete.add(f);
                    }
                }

                final FeaturePreprocessor fpp = new FeaturePreprocessor();
                String newFile = fpp.getCommitFileContent(featuresToDelete.toArray(new Feature[featuresToDelete.size()]),
                        "C:\\obermanndavid\\git-to-ecco\\test_repo\\test.cpp");

                System.out.println(newFile);

                commands.add(
                        new EccoCommit(featuresToCommit.toArray(new Feature[featuresToCommit.size()]))
                );*/
            }

        }

        int i = 0;
        System.out.println("---------------------------------------");
        for (EccoCommand command : commands) {
            System.out.println(i++ + " " + command.getCommandMsg());
        }

        gitHelper.checkOutCommit("master");

    }
    //Traverse preorder, later on post order might be the way to go (to get the deepest leaf, which contains the change)
    private static void traverse(TreeFeature tf, int lvl){
        for (int i = 0; i < lvl; i++) {
            System.out.print("-");
        }
        System.out.print(tf.toString() + "\n");

        for(TreeFeature f : tf.getChildren()){
            traverse(f, lvl+1);
        }
    }

}
