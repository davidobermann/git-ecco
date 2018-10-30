package at.jku.isse.gitecco.git;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

public interface GitCommitListener {
    void onCommit(GitCommit gc, GitCommitList gcl);
}
