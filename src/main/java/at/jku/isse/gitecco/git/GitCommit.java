package at.jku.isse.gitecco.git;

/**
 * Class for handling commits and be able to
 * distinguish between normal commits, branch points and merges.
 */
public class GitCommit {
    private final String commitName;
    private final GitCommitType type;

    /**
     * Creates a new GitCommit
     * @param commitName
     * @param type
     */
    public GitCommit(String commitName, GitCommitType type) {
        this.commitName = commitName;
        this.type = type;
    }

    /**
     * Gets the SHA1 ID aka the name of the commit.
     * @return the commit name / SHA1 ID
     */
    public String getCommitName() {
        return commitName;
    }

    /**
     * Gets the type of the commit: commit/branch/merge
     * @return
     */
    public GitCommitType getType() {
        return type;
    }
}
