package at.jku.isse.gitecco;

import at.jku.isse.gitecco.cdt.CDTHelper;
import at.jku.isse.gitecco.cdt.FeatureParser;
import at.jku.isse.gitecco.cdt.TreeFeature;
import at.jku.isse.gitecco.ecco.EccoCommit;
import at.jku.isse.gitecco.git.*;
import at.jku.isse.gitecco.preprocessor.FeaturePreprocessor;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.jgit.lib.Constants;

import java.nio.file.Files;
import java.nio.file.Paths;
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

        final String repositoryPath = "C:\\obermanndavid\\git-to-ecco\\test_repo";
        //final String repositoryPath = "C:\\obermanndavid\\git-ecco-test\\test1\\Unity";
        final GitHelper gitHelper = new GitHelper(repositoryPath);
        final GitCommitList commits = new GitCommitList(repositoryPath);

        //just to make sure it works
        gitHelper.checkOutCommit(Constants.HEAD);

        //Test for Listeners
        commits.addGitCommitListener(
                new GitCommitListener() {
                    @Override
                    public void onCommit(GitCommit gc, GitCommitList gcl) {

                        String code = "";
                        Change[] changes;
                        GitCommit oldGc = null;

                        if (gcl.size()>0) oldGc = gcl.get(gcl.size()-1);
                        gitHelper.checkOutCommit(gc.getCommitName());
                        try {
                            for (String cf : gitHelper.getChangedFiles(oldGc, gc)) {
                                if (FilenameUtils.getExtension(cf).equals("cpp")
                                        || FilenameUtils.getExtension(cf).equals("c")) {
                                    System.out.println(cf);
                                    List<String> codelist = Files.readAllLines(Paths.get(cf));
                                    code = codelist.stream().collect(Collectors.joining("\n"));

                                    final IASTTranslationUnit translationUnit = CDTHelper.parse(code.toCharArray());
                                    final IASTPreprocessorStatement[] ppstatements
                                            = translationUnit.getAllPreprocessorStatements();
                                    final FeatureParser featureParser = new FeatureParser();

                                    TreeFeature featureTree
                                            = featureParser.parseToTreeDef(ppstatements, codelist.size());

                                    //featureTree.printAll();

                                    changes = gitHelper.getFileDiffs(oldGc, gc, cf);

                                    //print changes
                                    System.out.print("Changes at:");
                                    for (Change change : changes) {
                                        System.out.print(change.toString()+"; ");
                                    }
                                    System.out.println();
                                    featureTree.linkChanges(changes);

                                    final List<TreeFeature> featuresToCommit = featureTree.getChangedAsList();

                                    final FeaturePreprocessor fpp = new FeaturePreprocessor();

                                    //replace filepath with "cf" variable later
                                    fpp.preprocess(featureTree, "<filepath>");

                                    System.out.println(
                                            new EccoCommit(featuresToCommit).getCommandMsg()
                                    );
                                } else {
                                    //binary file
                                    System.out.println(
                                            new EccoCommit(true).getCommandMsg()
                                    );
                                }
                                System.out.println("-----------------------");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        gitHelper.getAllCommits(commits);
        gitHelper.checkOutCommit(Constants.HEAD);

    }

}
