package at.jku.isse.gitecco;

import at.jku.isse.gitecco.git.GitCommit;
import at.jku.isse.gitecco.git.GitCommitList;
import at.jku.isse.gitecco.git.GitCommitListener;
import at.jku.isse.gitecco.git.GitHelper;
import at.jku.isse.gitecco.preprocessor.VariantGenerator;
import at.jku.isse.gitecco.tree.visitor.GetAllFeaturesVisitor;
import org.eclipse.jgit.lib.Constants;

import java.util.ArrayList;

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

        final String repositoryPath = "C:\\obermanndavid\\git-to-ecco\\test_repo2";
        //old repo: not suitable anymore since we assume there will be only true/false expressions.
        //final String repositoryPath = "C:\\obermanndavid\\git-to-ecco\\test_repo";
        //final String repositoryPath = "C:\\obermanndavid\\git-ecco-test\\test1\\Unity";

        //Stuff for testing out implemented git, parse and tree methods, etc.
        final GitHelper gitHelper = new GitHelper(repositoryPath);
        final GitCommitList commits = new GitCommitList(repositoryPath);

        gitHelper.checkOutCommit(Constants.MASTER);
        //commits.enableAutoCommitConfiguration();
        /*commits.addGitCommitListener(new GitCommitListener() {
            @Override
            public void onCommit(GitCommit gc, GitCommitList gcl) {
                final GetAllFeaturesVisitor v = new GetAllFeaturesVisitor();
                gc.getTree().accept(v);
                for (String feature : v.getAllFeatures()) {
                    System.out.println(feature);
                }
                System.out.println("-------------");
            }
        });*/
        gitHelper.getAllCommits(commits);

        /*final ArrayList<String> config = new ArrayList<String>();
        config.add("AA");
        config.add("A");
        config.add("C");

        final VariantGenerator vg = new VariantGenerator();

        vg.generateVariants(config, "C:\\obermanndavid\\git-ecco-test\\coantest",
                "C:\\obermanndavid\\git-ecco-test\\spin");
        */


    }

}
