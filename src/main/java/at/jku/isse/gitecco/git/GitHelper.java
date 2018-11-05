package at.jku.isse.gitecco.git;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
     * @param filePath The FilePath for which the Diff should be applied.
     * @return An Array of Changes which contains all the changes between the commits.
     * @throws Exception
     */
    public Change[] getFileDiffs(GitCommit oldCommit, GitCommit newCommit, String filePath) throws Exception {

        String filterPath = filePath.substring(pathUrl.length()+1).replace("\\", "/");

        List<DiffEntry> diff = git.diff().
                setOldTree(prepareTreeParser(git.getRepository(), oldCommit)).
                setNewTree(prepareTreeParser(git.getRepository(), newCommit)).
                setPathFilter(PathFilter.create(filterPath)).
                call();

        //to filter on Suffix use the following instead
        //setPathFilter(PathSuffixFilter.create(".cpp"))

        List<Change> changes = new ArrayList<Change>();
        ByteArrayOutputStream diffStream = new ByteArrayOutputStream();
        DiffParser fileDiffParser = new DiffParser();

        for (DiffEntry entry : diff) {
            diffStream.reset();
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
        if(changes.size() == 0) {
            changes.add(new Change(0, Files.readAllLines(Paths.get(filePath)).size()));
        }
        return changes.toArray(new Change[changes.size()]);
    }

    /**
     * Returns all paths to files that have changed in the repo between 2 commits.
     * @param oldCommit
     * @param newCommit
     * @return List of Strings of paths.
     * @throws IOException
     * @throws GitAPIException
     */
    public List<String> getChangedFiles(GitCommit oldCommit, GitCommit newCommit) throws IOException, GitAPIException {
        final List<String> paths = new ArrayList<>();
        List<DiffEntry> diffs = git.diff().
                setOldTree(prepareTreeParser(git.getRepository(), oldCommit)).
                setNewTree(prepareTreeParser(git.getRepository(), newCommit)).
                call();
        for (DiffEntry entry : diffs) {
            paths.add(pathUrl + "\\" + entry.getNewPath().replace('/','\\'));
        }
        return Collections.unmodifiableList(paths);
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
     * Method to retrieve all commits form a repository and put it to a GitCommitList.
     * @param commits the GitCommitList to which the commits a re saved to.
     * @return The GitCommitList which was passed to the method.
     * @throws GitAPIException
     * @throws IOException
     */
    public GitCommitList getAllCommits(GitCommitList commits) throws GitAPIException, IOException {
        final List<GitCommitType> types = new ArrayList<>();
        final String[] commitNames = getAllCommitNames();
        final Repository repo = git.getRepository();
        final PlotWalk revWalk = new PlotWalk(repo);
        final RevCommit root = revWalk.parseCommit(repo.resolve("refs/heads/master"));
        revWalk.markStart(root);

        PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<>();
        plotCommitList.source(revWalk);
        plotCommitList.fillTo(Integer.MAX_VALUE);
        Collections.reverse(plotCommitList);

        for (int i = 0; i < commitNames.length; i++) {
            types.clear();
            types.add(GitCommitType.COMMIT);
            if(plotCommitList.get(i).getChildCount() > 1
                    || plotCommitList.get(i).getChildCount() == 0 &&  plotCommitList.get(i).getChildCount() > 0) {
                types.add(GitCommitType.BRANCH);
            }
            if(plotCommitList.get(i).getParentCount() > 1) {
                types.add(GitCommitType.MERGE);
            }
            String branch = getBranchOfCommit(commitNames[i]);
            commits.add(new GitCommit(commitNames[i], new ArrayList<GitCommitType>(types), branch), commits);
        }

        return commits;
    }

    private String getBranchOfCommit(String commit) throws MissingObjectException, GitAPIException {
        Map<ObjectId, String> map = git
                .nameRev()
                .addPrefix( "refs/heads" )
                .add(ObjectId.fromString(commit))
                .call();

        return map.isEmpty() ? "" : map.get(ObjectId.fromString(commit)).split("~")[0];
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

        if(gitcommit == null) return new EmptyTreeIterator();

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
