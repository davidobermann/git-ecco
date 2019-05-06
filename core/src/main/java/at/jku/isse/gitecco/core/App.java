package at.jku.isse.gitecco.core;

import at.jku.isse.gitecco.core.git.GitCommitList;
import at.jku.isse.gitecco.core.git.GitHelper;
import at.jku.isse.gitecco.core.tree.nodes.RootNode;

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
        //final String repositoryPath = "C:\\obermanndavid\\git-to-ecco\\test_repo3";
        final String repositoryPath = "C:\\obermanndavid\\git-to-ecco\\test_repo5";
        //final String repositoryPath = "C:\\obermanndavid\\git-ecco-test\\test1\\Unity";

        //final String repositoryPath = "C:\\obermanndavid\\git-ecco-test\\test2\\betaflight";
        //final String repositoryPath = "C:\\obermanndavid\\git-to-ecco\\test_repo4";
        final GitHelper gitHelper = new GitHelper(repositoryPath);
        final GitCommitList commits = new GitCommitList(repositoryPath);
        //commits.enableAutoCommitConfigForEveryModelAndCntArtifacts();
        gitHelper.getAllCommits(commits);
    }
}
