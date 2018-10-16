package at.jku.isse.gitecco.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * HelperClass for working with JGit.
 */
public class GitHelper {

    private final Git git;
    private final String pathUrl;

    /**
     * Creates a new instance of GitHelper and clones the specified
     * repo form the url String to the path String.
     * @param url The URL of the git repository to be cloned
     * @param path The path string, where the repository should be cloned to.
     * @throws Exception
     */
    public GitHelper(String url, String path) throws Exception {
        git = cloneRepo(url, path);
        pathUrl = path;
    }

    /**
     * Creates a new instance of GitHelper by opening an existing repository.
     * at the given path.
     * Note that the repository needs to be existing already.
     * @param path The path String to the existing repository.
     * @throws IOException
     */
    public GitHelper(String path) throws IOException {
        git = openRepo(path);
        pathUrl = path;
    }

    /**
     * Gets the Diff between two Commits specified by their commit names.
     * The Diff is stored as a <code>Change</code>.
     * All the changes will be returned as an Array.
     * @param newCommit The name of the newer commit
     * @param oldCommit The name of the older commit.
     * @return An Array of Changes which contains all the changes between the commits.
     * @throws Exception
     */
    public Change[] getFileDiffs(GitCommit oldCommit, GitCommit newCommit) throws Exception {

        List<DiffEntry> diff = git.diff().
                setOldTree(prepareTreeParser(git.getRepository(), oldCommit)).
                setNewTree(prepareTreeParser(git.getRepository(), newCommit))
                .call();

        // to filter on Suffix use the following instead
        //setPathFilter(PathSuffixFilter.create(".cpp"))

        List<Change> changes = new ArrayList<Change>();
        ByteArrayOutputStream diffStream = new ByteArrayOutputStream();
        DiffParser fileDiffParser = new DiffParser();

        for (DiffEntry entry : diff) {
            diffStream.reset();
            //System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());

            try (DiffFormatter formatter = new DiffFormatter(diffStream)) {
                formatter.setRepository(git.getRepository());
                formatter.setContext(0);
                formatter.format(entry);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(diffStream.size() > 0) {
                if(fileDiffParser.parse(diffStream.toString())){
                    for(Change r:fileDiffParser.getplusRanges()){
                        changes.add(r);
                    }
                }
            }
            fileDiffParser.reset();
        }
        return changes.toArray(new Change[changes.size()]);
    }


    private Git openRepo(String dirPath) throws IOException {
        File dir = new File(dirPath);
        Git git = Git.open(dir);

        return git;
    }

    /**
     * Checks out a commit by the given name.
     * Does this by using the runtime execution since JGit is buggy
     * when it comes to checkouts and cleans, etc.
     * @param name The name of the commit, which should be checked out.
     */
    public void checkOutCommit(String name){
        System.out.println("Checking out commit: " + name
                + "\n at " + pathUrl);

        Process p;
        try {
            p = Runtime.getRuntime().exec(String.format("git -C %s clean --force",this.pathUrl));
            p.waitFor();
            p = Runtime.getRuntime().exec(String.format("git -C %s reset --hard",this.pathUrl));
            p.waitFor();
            p = Runtime.getRuntime().exec(String.format("git -C %s checkout %s",this.pathUrl,name));
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Git cloneRepo(String url, String dirPath) throws Exception {
        File dir = new File(dirPath);
        System.out.println("Cloning from " + url + " to " + dir);

        Git git = Git.cloneRepository()
                .setURI(url)
                .setDirectory(dir)
                .call();

        System.out.println("Having repository: " + git.getRepository().getDirectory() + "\n");

        return git;
    }

    /**
     * Lists all the Diffs between two Commits.
     * @param newCommit The newer commit name.
     * @param oldCommit The older commit name.
     * @throws GitAPIException
     * @throws IOException
     */
    public void listDiff(GitCommit oldCommit, GitCommit newCommit) throws GitAPIException, IOException {
        final List<DiffEntry> diffs = git.diff()
                .setOldTree(prepareTreeParser(git.getRepository(), oldCommit))
                .setNewTree(prepareTreeParser(git.getRepository(), newCommit))
                .call();

        System.out.println("Found: " + diffs.size() + " differences");
        for (DiffEntry diff : diffs) {
            System.out.println("Diff: " + diff.getChangeType() + ": " +
                    (diff.getOldPath().equals(diff.getNewPath()) ? diff.getNewPath() : diff.getOldPath() + " -> " + diff.getNewPath()));
        }
    }


    /**
     * Gets all commit names of all the commits of the opened repository.
     * @return String[] of alle the commit names.
     * @throws GitAPIException
     */
    private String[] getAllCommitNames() throws GitAPIException {
        ArrayList<String> commitNames = new ArrayList();
        Iterable<RevCommit> log = git.log().call();

        for (RevCommit rc : log) {
            commitNames.add(rc.getName());
        }

        Collections.reverse(commitNames);
        return commitNames.toArray(new String[commitNames.size()]);
    }

    /**
     * Retrieves all of the commits including their type (commit, branch, merge)
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    public List<GitCommit> getAllCommits() throws GitAPIException, IOException {
        List<GitCommit> commits = new ArrayList<>();
        String[] commitNames = getAllCommitNames();
        Repository repo = git.getRepository();
        PlotWalk revWalk = new PlotWalk(repo);
        RevCommit root = revWalk.parseCommit(repo.resolve("refs/heads/master"));
        revWalk.markStart(root);

        PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<>();
        plotCommitList.source(revWalk);
        plotCommitList.fillTo(Integer.MAX_VALUE);
        Collections.reverse(plotCommitList);

        for (int i = 0; i < commitNames.length; i++) {
            GitCommitType type =
                    plotCommitList.get(i).getChildCount() > 1 ? GitCommitType.BRANCH : GitCommitType.COMMIT;
            commits.add(new GitCommit(commitNames[i], type));
            //plotCommitList.get(i).getParentCount();
            //TODO: if parent count is > 1 then type is merge
            //Can there be a merge and a branch point be at the same time?
        }

        return Collections.unmodifiableList(commits);
    }

    /**
     * Should determine to which branch a feature belongs.
     * Not working yet!!!
     * @param commitName
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    public String belongsToBranch(String commitName) throws GitAPIException, IOException {
        List<Ref> branches = git.branchList().call();
        Repository repo = git.getRepository();
        RevWalk walk = new RevWalk(repo);
        RevCommit commit = walk.parseCommit(repo.resolve(commitName));
        String retval = "";
        for (Ref branch : branches) {
            String branchName = branch.getName();
            boolean foundInThisBranch = false;
            for (Map.Entry<String, Ref> e : repo.getAllRefs().entrySet()) {
                if (e.getKey().startsWith(Constants.R_HEADS)) {
                    if (walk.isMergedInto(commit, walk.parseCommit(
                            e.getValue().getObjectId()))) {
                        String foundInBranch = e.getValue().getName();
                        if (branchName.equals(foundInBranch)) {
                            foundInThisBranch = true;
                            retval = branchName;
                            break;
                        }
                    }
                }
            }

            if (foundInThisBranch) {
                System.out.println(commit.getName());
                System.out.println(branchName);
                System.out.println(commit.getAuthorIdent().getName());
                System.out.println(new Date(commit.getCommitTime() * 1000L));
                System.out.println(commit.getFullMessage());
            }
        }


        return retval;
    }


    private AbstractTreeIterator prepareTreeParser(Repository repository, GitCommit gitcommit) throws IOException {
        String objectId = gitcommit.getCommitName();
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }

}
