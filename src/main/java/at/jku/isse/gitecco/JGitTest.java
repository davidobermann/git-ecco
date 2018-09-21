package at.jku.isse.gitecco;

import at.jku.isse.gitecco.git.Change;
import at.jku.isse.gitecco.git.DiffParser;
import at.jku.isse.gitecco.git.GitHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JGitTest {
    public static void main(String args[]) throws Exception {
        final GitHelper gitHelper = new GitHelper("C:\\obermanndavid\\git-to-ecco\\test_repo");
        final String[] commits = gitHelper.getAllCommitNames();

        for (String commit : commits) {
            System.out.println(commit);
        }

        for (String commit : commits) {
            System.out.println("-----------------------------");
            gitHelper.checkOutCommit(commit);
            System.out.println(
                    Files.readAllLines(
                            (Paths.get(
                                    "C:\\obermanndavid\\git-to-ecco\\test_repo\\test.cpp")))
                            .stream()
                            .collect(Collectors.joining("\n"))
            );
        }

    }


    private static void cloneRepo() throws Exception {
        //https://github.com/zpqrtbnk/test-repo.git
        String url = "https://github.com/centic9/jgit-cookbook.git";
        File dir = new File("C:\\obermanndavid\\git-to-ecco\\test_repo");
        Git git = Git.open(dir);
        //System.out.println("Cloning from " + url + " to " + dir);
        /*Git git = Git.cloneRepository()
                .setURI(url)
                .setDirectory(dir)
                .call();*/

        System.out.println("Having repository: " + git.getRepository().getDirectory() + "\n");


        //git.checkout().setName("initialtag").call();
        Iterable<RevCommit> log = git.log().call();
        System.out.println("Commits:");
        int x = 0;
        for(RevCommit rc: log){
            x++;
            System.out.println(rc.getName());
        }
        System.out.println("Number of commits: " + x + "\n");

        String[] commits = getAllCommitNames(git);

        System.out.println();
        for (int i = 1; i < commits.length; i++) {
            listDiff(git.getRepository(), git, commits[i-1], commits[i]);
            System.out.println("---");
        }

        System.out.println("-------------------");

        for (int i = 1; i < commits.length; i++) {
            showFileDiff(git, commits[i-1],commits[i]);
            System.out.println("---");
        }

    }

    private static void showFileDiff(Git git, String newCommit, String oldCommit) throws Exception {
        List<DiffEntry> diff = git.diff().
                setOldTree(prepareTreeParser(git.getRepository(), oldCommit)).
                setNewTree(prepareTreeParser(git.getRepository(), newCommit))
                .call();

        // to filter on Suffix use the following instead
        //setPathFilter(PathSuffixFilter.create(".cpp"))

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

                } else
                    System.out.println("no matches");
            }
        }

        //System.out.println(diffStream.toString());
        diffStream.flush();
        diffStream.close();
        String diffString = diffStream.toString();

    }

    private static void listDiff(Repository repository, Git git, String newCommit, String oldCommit) throws GitAPIException, IOException {
        final List<DiffEntry> diffs = git.diff()
                .setOldTree(prepareTreeParser(repository, oldCommit))
                .setNewTree(prepareTreeParser(repository, newCommit))
                .call();

        System.out.println("Found: " + diffs.size() + " differences");
        for (DiffEntry diff : diffs) {
            System.out.println("Diff: " + diff.getChangeType() + ": " +
                    (diff.getOldPath().equals(diff.getNewPath()) ? diff.getNewPath() : diff.getOldPath() + " -> " + diff.getNewPath()));
        }
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
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

    private static String[] getAllCommitNames(Git git) throws GitAPIException {
        ArrayList<String> commitNames = new ArrayList();
        Iterable<RevCommit> log = git.log().call();

        for (RevCommit rc : log) {
            commitNames.add(rc.getName());
        }

        return commitNames.toArray(new String[commitNames.size()]);
    }

}