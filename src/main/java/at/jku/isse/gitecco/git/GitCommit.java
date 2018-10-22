package at.jku.isse.gitecco.git;

import java.util.Collections;
import java.util.List;

/**
 * Class for handling commits and be able to
 * distinguish between normal commits, branch points and merges.
 */
public class GitCommit {
    private final String commitName;
    private final List<GitCommitType> types;

    /**
     * Creates a new GitCommit
     * @param commitName
     * @param types
     */
    public GitCommit(String commitName, List<GitCommitType> types) {
        this.commitName = commitName;
        this.types = types;
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
    public List<GitCommitType> getType() {
        return Collections.unmodifiableList(types);
    }
}
