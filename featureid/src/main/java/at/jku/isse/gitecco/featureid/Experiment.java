package at.jku.isse.gitecco.featureid;


import at.jku.isse.gitecco.core.git.GitCommitList;
import at.jku.isse.gitecco.core.git.GitHelper;
import at.jku.isse.gitecco.core.type.TraceableFeature;
import at.jku.isse.gitecco.featureid.identification.ID;

import java.util.ArrayList;
import java.util.List;

public class Experiment {

    private final static String REPO_PATH = "C:\\obermanndavid\\git-to-ecco\\test_repo5";
    private final static boolean DISPOSE = true;

    public static void main(String... args) throws Exception {
        final GitHelper gitHelper = new GitHelper(REPO_PATH);
        final GitCommitList commitList = new GitCommitList(REPO_PATH);
        final List<TraceableFeature> evaluation = new ArrayList<>();

        commitList.addGitCommitListener(
                (gc, gcl) -> {
                    ID.evaluateFeatureMap(evaluation, ID.id(gc.getTree()));
                    //dispose tree if it is not needed -> for memory saving reasons.
                    if (DISPOSE) gc.disposeTree();
                }
        );

        gitHelper.getAllCommits(commitList);

        System.out.println("end");
    }

}
