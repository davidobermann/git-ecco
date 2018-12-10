package at.jku.isse.gitecco.git;

import at.jku.isse.gitecco.tree.nodes.RootNode;

import java.util.Collections;
import java.util.List;

/**
 * Class for handling commits and be able to
 * distinguish between normal commits, branch points and merges.
 */
public class GitCommit {
    private RootNode tree;
    private final String commitName;
    private final String diffCommit;
    private final List<GitCommitType> types;
    private final String branch;

    /**
     * Creates a new GitCommit
     *
     * @param commitName
     * @param types
     * @param branch
     */
    public GitCommit(String commitName, String diffCommit, List<GitCommitType> types, String branch) {
        this.commitName = commitName;
        this.types = types;
        this.branch = branch;
        this.diffCommit = diffCommit;
    }

    /**
     * Gets the tree of a commit
     * @return
     */
    public RootNode getTree(){
        return tree;
    }

    /**
     * sets the tree once, a second call is not effective
     * @param n
     */
    public void setTree(RootNode n) {
        if(tree == null) tree = n;
    }

    /**
     * Gets the branch of the commit
     *
     * @return
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Gets the SHA1 ID aka the name of the commit to diff with
     *
     * @return the commit name / SHA1 ID
     */
    public String getDiffCommitName() {
        return diffCommit;
    }

    /**
     * Gets the SHA1 ID aka the name of the commit.
     *
     * @return the commit name / SHA1 ID
     */
    public String getCommitName() {
        return commitName;
    }

    /**
     * Gets the type of the commit: commit/branch/merge
     *
     * @return
     */
    public List<GitCommitType> getType() {
        return Collections.unmodifiableList(types);
    }
}
