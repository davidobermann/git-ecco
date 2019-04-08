package at.jku.isse.gitecco.core.git;

public interface GitCommitListener {
    void onCommit(GitCommit gc, GitCommitList gcl);
}
