package at.jku.isse.gitecco.git;

public interface GitMergeListener {
    void onMerge(GitCommit gc);
}
