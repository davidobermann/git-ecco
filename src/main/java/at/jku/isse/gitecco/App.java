package at.jku.isse.gitecco;

import at.jku.isse.gitecco.git.GitCommitList;
import at.jku.isse.gitecco.git.GitHelper;

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
        //final String repositoryPath = "C:\\obermanndavid\\git-to-ecco\\test_repo2";
        //old repo: not suitable anymore since we assume there will be only true/false expressions.
        final String repositoryPath = "C:\\obermanndavid\\git-to-ecco\\test_repo3";
        //final String repositoryPath = "C:\\obermanndavid\\git-ecco-test\\test1\\Unity";

        //Stuff for testing out implemented git, parse and tree methods, etc.
        final GitHelper gitHelper = new GitHelper(repositoryPath);
        final GitCommitList commits = new GitCommitList(repositoryPath);
        //gitHelper.checkOutCommit(Constants.MASTER);
        //commits.enableAutoCommitConfigForEveryModel();
        //commits.enableAutoCommitConfig();
        commits.enableAutoCommitConfigForEveryModelAndCntArtifacts();
        gitHelper.getAllCommits(commits);
    }

}
