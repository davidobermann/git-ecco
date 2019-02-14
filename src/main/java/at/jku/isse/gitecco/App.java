package at.jku.isse.gitecco;

import at.jku.isse.gitecco.git.GitCommit;
import at.jku.isse.gitecco.git.GitCommitList;
import at.jku.isse.gitecco.git.GitHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.util.Collection;

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


        /*try (Repository repository = Git.open(new File(repositoryPath)).getRepository()) {
            // get a list of all known heads, tags, remotes, ...
            Collection<Ref> allRefs = repository.getAllRefs().values();

            // a RevWalk allows to walk over commits based on some filtering that is defined
            try (RevWalk revWalk = new RevWalk( repository )) {
                for( Ref ref : allRefs ) {
                    revWalk.markStart( revWalk.parseCommit( ref.getObjectId() ));
                }
                System.out.println("Walking all commits starting with " + allRefs.size() + " refs: " + allRefs);
                int count = 0;
                for( RevCommit commit : revWalk ) {
                    System.out.println("Commit: " + commit);
                    count++;
                }
                System.out.println("Had " + count + " commits");
            }
        }*/


        //Stuff for testing out implemented git, parse and tree methods, etc.
        final GitHelper gitHelper = new GitHelper(repositoryPath);
        final GitCommitList commits = new GitCommitList(repositoryPath);
        //gitHelper.checkOutCommit(Constants.MASTER);
        gitHelper.getAllCommits(commits);
        System.out.println(commits.size());
        //commits.enableAutoCommitConfig();
        //gitHelper.getAllCommits(commits);

    }

}
