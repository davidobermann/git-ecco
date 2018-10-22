package at.jku.isse.gitecco.git;

public interface GitBranchListener {
    void onBranch(GitCommit gc);
}
