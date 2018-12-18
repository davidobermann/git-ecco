package at.jku.isse.gitecco.git;

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
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
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
     *
     * @param url  The URL of the git repository to be cloned
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
     *
     * @param path The path String to the existing repository.
     * @throws IOException
     */
    public GitHelper(String path) throws IOException {
        git = openRepo(path);
        pathUrl = path;
    }

    /**
     * Gets the path in which the repository is stored
     *
     * @return
     */
    public String getPath() {
        return this.pathUrl;
    }

    /**
     * Gets the Diff between two Commits specified by their commit names.
     * The Diff is stored as a <code>Change</code>.
     * All the changes will be returned as an Array.
     *
     * @param newCommit The commit which should be diffed --> also contains the parent to diff with
     * @param filePath  The FilePath for which the Diff should be applied.
     * @return An Array of Changes which contains all the changes between the commits.
     * @throws Exception
     */
    public Change[] getFileDiffs(GitCommit newCommit, String filePath) throws Exception {

        //prepare for file path filter.
        //String filterPath = filePath.substring(pathUrl.length()+1).replace("\\", "/");
        List<DiffEntry> diff = git.diff().
                setOldTree(prepareTreeParser(git.getRepository(), newCommit.getDiffCommitName())).
                setNewTree(prepareTreeParser(git.getRepository(), newCommit.getCommitName())).
                setPathFilter(PathFilter.create(filePath)).
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

            if (diffStream.size()>0) {
                if (fileDiffParser.parse(diffStream.toString())) {
                    for (Change r : fileDiffParser.getplusRanges()) {
                        changes.add(r);
                    }
                }
            }
            fileDiffParser.reset();
        }

        filePath = pathUrl + "\\" + filePath;

        if (changes.size() == 0) changes.add(new Change(0, Files.readAllLines(Paths.get(filePath)).size()));

        return changes.toArray(new Change[changes.size()]);
    }

    /**
     * Returns all paths to files that have changed in the repo between 2 commits.
     *
     * @param newCommit commit to diff with --> also contains the parent for diffing
     * @return List of Strings of paths.
     * @throws IOException
     * @throws GitAPIException
     */
    public List<String> getChangedFiles(GitCommit newCommit) throws IOException, GitAPIException {
        final List<String> paths = new ArrayList<>();
        List<DiffEntry> diffs = git.diff().
                setOldTree(prepareTreeParser(git.getRepository(), newCommit.getDiffCommitName())).
                setNewTree(prepareTreeParser(git.getRepository(), newCommit.getCommitName())).
                call();
        for (DiffEntry entry : diffs) {
            if(entry.getChangeType() != DiffEntry.ChangeType.DELETE) {
                //if needed prepend pathURL + "\\" to get the absolute path
                paths.add(entry.getNewPath().replace('/', '\\'));
            }
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
     *
     * @param name The name of the commit, which should be checked out.
     */
    public void checkOutCommit(String name) {
        /*System.out.println("Checking out commit: "+name
                +"\n at "+pathUrl);*/

        Process p;
        try {
            p = Runtime.getRuntime().exec(String.format("git -C %s clean --force", this.pathUrl));
            p.waitFor();
            p = Runtime.getRuntime().exec(String.format("git -C %s reset --hard", this.pathUrl));
            p.waitFor();
            p = Runtime.getRuntime().exec(String.format("git -C %s checkout %s", this.pathUrl, name));
            p.waitFor();
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Git cloneRepo(String url, String dirPath) throws Exception {
        File dir = new File(dirPath);
        System.out.println("Cloning from "+url+" to "+dir);

        Git git = Git.cloneRepository()
                .setURI(url)
                .setDirectory(dir)
                .call();

        System.out.println("Having repository: "+git.getRepository().getDirectory()+"\n");

        return git;
    }

    /**
     * Lists all the Diffs between two Commits.
     *
     * @param newCommit The new commit which should be diffed --> also contains the parent to diff with.
     * @throws GitAPIException
     * @throws IOException
     */
    public void listDiff(GitCommit newCommit) throws GitAPIException, IOException {
        final List<DiffEntry> diffs = git.diff()
                .setOldTree(prepareTreeParser(git.getRepository(), newCommit.getDiffCommitName()))
                .setNewTree(prepareTreeParser(git.getRepository(), newCommit.getCommitName()))
                .call();

        System.out.println("Found: "+diffs.size()+" differences");
        for (DiffEntry diff : diffs) {
            System.out.println("Diff: "+diff.getChangeType()+": "+
                    (diff.getOldPath().equals(diff.getNewPath()) ? diff.getNewPath() : diff.getOldPath()+" -> "+diff.getNewPath()));
        }
    }


    /**
     * Gets all commit names of all the commits of the opened repository.
     *
     * @return String[] of alle the commit names.
     * @throws GitAPIException
     */
    public String[] getAllCommitNames() throws GitAPIException {
        ArrayList<String> commitNames = new ArrayList();
        Iterable<RevCommit> log = git.log().call();

        for (RevCommit rc : log) {
            commitNames.add(rc.getName());
        }

        Collections.reverse(commitNames);
        return commitNames.toArray(new String[commitNames.size()]);
    }

    public String[] getAllCommitNamesNew() throws GitAPIException, IOException {
        List<String> commitNames = new ArrayList();
        Repository repository = git.getRepository();

        Collection<Ref> allRefs = repository.getAllRefs().values();

        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk revWalk = new RevWalk( repository )) {

            for(Ref ref : allRefs) {
                revWalk.markStart(revWalk.parseCommit(ref.getObjectId()));
            }
            for(RevCommit rc : revWalk) {
                System.out.println(rc.getName());
                try {
                    System.out.println("Diff to: " + rc.getParent(0).getName());
                }catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Diff to: zero commit --> no parrent available");
                }
                //commitNames.add(rc.getName());
            }
        }
        Collections.reverse(commitNames);
        return commitNames.toArray(new String[commitNames.size()]);
    }

    /**
     * Method to retrieve all commits form a repository and put it to a GitCommitList.
     *
     * @param commits the GitCommitList to which the commits a re saved to.
     * @return The GitCommitList which was passed to the method.
     * @throws GitAPIException
     * @throws IOException
     */
    public GitCommitList getAllCommitsNew(GitCommitList commits) throws Exception {
        Repository repository = git.getRepository();
        final List<GitCommitType> types = new ArrayList<>();
        Collection<Ref> allRefs = repository.getAllRefs().values();

        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk revWalk = new RevWalk( repository )) {

            PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<>();
            plotCommitList.source(revWalk);
            plotCommitList.fillTo(Integer.MAX_VALUE);
            Collections.reverse(plotCommitList);

            for(Ref ref : allRefs) {
                revWalk.markStart(revWalk.parseCommit(ref.getObjectId()));
            }

            int i = 0;

            for(RevCommit rc : revWalk) {
                System.out.println(rc.getName());
                try {

                    types.clear();
                    types.add(GitCommitType.COMMIT);

                    if (rc.getParentCount()>1
                            || plotCommitList.get(i).getChildCount() == 0 && plotCommitList.get(i).getChildCount()>0) {
                        types.add(GitCommitType.BRANCH);
                    }

                    if (plotCommitList.get(i).getParentCount()>1) {
                        types.add(GitCommitType.MERGE);
                    }

                    String branch = getBranchOfCommit(plotCommitList.get(i).getName());
                    //commits.add(new GitCommit(commitNames[i], new ArrayList<GitCommitType>(types), branch), commits);
                    System.out.println("added commit as expected to the list.");

                }catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Diff to: zero commit --> no parrent available");
                }
                i++;
            }
        }

        return commits;
    }

    /**
     * Method to retrieve all commits form a repository and put it to a GitCommitList.
     *
     * @param commits the GitCommitList to which the commits a re saved to.
     * @return The GitCommitList which was passed to the method.
     * @throws GitAPIException
     * @throws IOException
     */
    public GitCommitList getAllCommits(GitCommitList commits) throws Exception {
        final List<GitCommitType> types = new ArrayList<>();
        final String[] commitNames = getAllCommitNames();
        final Repository repo = git.getRepository();
        final PlotWalk revWalk = new PlotWalk(repo);
        final RevCommit root = revWalk.parseCommit(repo.resolve("refs/heads/master"));
        revWalk.markStart(root);
        revWalk.sort(RevSort.TOPO, true);
        revWalk.sort(RevSort.REVERSE, true);

        PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<>();
        plotCommitList.source(revWalk);
        plotCommitList.fillTo(Integer.MAX_VALUE);

        for (int i = 0; i<commitNames.length; i++) {
            types.clear();
            types.add(GitCommitType.COMMIT);

            if (plotCommitList.get(i).getChildCount()>1
                    || plotCommitList.get(i).getChildCount() == 0 && plotCommitList.get(i).getChildCount()>0) {
                types.add(GitCommitType.BRANCH);
            }

            if (plotCommitList.get(i).getParentCount()>1) {
                types.add(GitCommitType.MERGE);
            }

            String branch = getBranchOfCommit(commitNames[i]);
            String parent;

            try {
                parent = plotCommitList.get(i).getParent(0).getName();
            } catch(ArrayIndexOutOfBoundsException aioobe){
                parent = "NULLCOMMIT";
            }

            commits.add(new GitCommit(plotCommitList.get(i).getName(), parent,
                    new ArrayList<GitCommitType>(types),
                    branch, plotCommitList.get(i)),
                    commits);
        }

        return commits;
    }

    /**
     * Retrieves the file paths of the files in the repository for a given commit
     * @param commit
     * @return List of paths as Strings
     * @throws IOException
     */
    public List<String> getRepositoryContents(GitCommit commit) throws IOException {
        final List<String> files = new ArrayList<>();

        RevWalk walk = new RevWalk(git.getRepository());

        RevCommit revCommit = walk.parseCommit(git.getRepository().resolve(commit.getCommitName()));
        RevTree tree = revCommit.getTree();

        TreeWalk treeWalk = new TreeWalk(git.getRepository());
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);

        while (treeWalk.next()) {
            files.add(treeWalk.getPathString());
        }

        return Collections.unmodifiableList(files);

    }

    private String getBranchOfCommit(String commit) throws MissingObjectException, GitAPIException {
        Map<ObjectId, String> map = git
                .nameRev()
                .addPrefix("refs/heads")
                .add(ObjectId.fromString(commit))
                .call();

        return map.isEmpty() ? "" : map.get(ObjectId.fromString(commit)).split("~")[0];
    }


    private AbstractTreeIterator prepareTreeParser(Repository repository, String commitName) throws IOException {
        if (commitName == null || commitName.equals("NULLCOMMIT")) return new EmptyTreeIterator();

        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(commitName));
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
