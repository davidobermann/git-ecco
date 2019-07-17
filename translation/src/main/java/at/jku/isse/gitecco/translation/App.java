package at.jku.isse.gitecco.translation;

import at.jku.isse.gitecco.core.git.GitCommitList;
import at.jku.isse.gitecco.core.git.GitHelper;
import at.jku.isse.gitecco.translation.constraintcomputation.util.GetNodesForChangeVisitor;

public class App {

	public static void main(String... args) throws Exception{
	    //TODO: planned arguments: DEBUG, dispose tree, max commits, repo path, csv path(feature id), outpath for ecco
        if(args.length < 1) {
            System.err.println("Two few arguments\n");
            System.exit(-1);
        }

	    String repoPath = args[0];

        final GitHelper gitHelper = new GitHelper(repoPath);
        final GitCommitList commitList = new GitCommitList(repoPath);

        commitList.addGitCommitListener((gc,gcl)-> {

            gitHelper.checkOutCommit(gc);
            //TODO:do the git commit and measure time or whatever

            GetNodesForChangeVisitor v = new GetNodesForChangeVisitor();

            

            //set change --> traverse --> retrieve nodes for change --> repeat
            gc.getTree().accept(v);

        });




    }

}
