package at.jku.isse.gitecco.git;

public interface GitCommitListener {
    void onCommit(GitCommit gc);
}
