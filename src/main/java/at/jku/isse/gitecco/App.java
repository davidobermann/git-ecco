package at.jku.isse.gitecco;

import at.jku.isse.gitecco.git.GitCommitList;
import at.jku.isse.gitecco.git.GitHelper;
import org.eclipse.jgit.lib.Constants;

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

        gitHelper.checkOutCommit(Constants.MASTER);

        gitHelper.getAllCommits(commits);
        gitHelper.checkOutCommit(Constants.MASTER);

    }

}
