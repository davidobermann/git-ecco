package at.jku.isse.gitecco.core.git;

import at.jku.isse.gitecco.core.cdt.CDTHelper;
import at.jku.isse.gitecco.core.cdt.FeatureParser;
import at.jku.isse.gitecco.core.tree.nodes.BinaryFileNode;
import at.jku.isse.gitecco.core.tree.nodes.RootNode;
import at.jku.isse.gitecco.core.tree.nodes.SourceFileNode;
import at.jku.isse.gitecco.core.tree.util.ComittableChange;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.runtime.CoreException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class for storing a row of commits
 * With the option to react to added commits
 * depending on their types.
 */
public class GitCommitList extends ArrayList<GitCommit> {
    private final GitHelper gitHelper;
    private final List<GitCommitListener> observersC = new ArrayList();
    private final List<GitBranchListener> observersB = new ArrayList();
    private final List<GitMergeListener> observersM = new ArrayList();

    public GitCommitList(String repoPath) throws IOException {
        super();
        this.gitHelper = new GitHelper(repoPath);
    }

    /**
     * Adds a GitCommitListener to the Object.
     *
     * @param gcl
     */
    public void addGitCommitListener(GitCommitListener gcl) {
        observersC.add(gcl);
    }

    /**
     * Adds a GitMergeListener to the Object.
     *
     * @param gml
     */
    public void addGitMergeListener(GitMergeListener gml) {
        observersM.add(gml);
    }

    /**
     * Adds a GitBranchListener to the Object.
     *
     * @param gbl
     */
    public void addGitBranchListener(GitBranchListener gbl) {
        observersB.add(gbl);
    }


    /**
     * Dummy method which blocks unobserved adding.
     *
     * @param gitCommit
     * @return
     */
    @Override
    public boolean add(GitCommit gitCommit) {
        final RootNode tree = new RootNode(gitHelper.getPath());
        final List<ComittableChange> committableChanges = new ArrayList<>();
        gitHelper.checkOutCommit(gitCommit.getCommitName());

        for (String file : gitHelper.getRepositoryContents(gitCommit)) {
            //source file or binary file --> with parsing, without.
            if (FilenameUtils.getExtension(file).equals("cpp")
                    || FilenameUtils.getExtension(file).equals("c")
                    || FilenameUtils.getExtension(file).equals("h")
                    || FilenameUtils.getExtension(file).equals("hpp")
                    || FilenameUtils.getExtension(file).equals("hh")) {


                final SourceFileNode fn = new SourceFileNode(tree, file);

                final String path = gitHelper.getPath()+"\\"+file;
                List<String> codelist = null;
                try {
                    codelist = Files.readAllLines(Paths.get(path), StandardCharsets.ISO_8859_1);
                } catch (IOException e1) {
                    System.err.println("error reading file: "+file);
                    e1.printStackTrace();
                }
                final String code = codelist.stream().collect(Collectors.joining("\n"));

                //file parsing
                IASTTranslationUnit translationUnit = null;
                try {
                    translationUnit = CDTHelper.parse(code.toCharArray());
                } catch (CoreException e1) {
                    System.err.println("error parsing with CDT Core: "+file);
                    e1.printStackTrace();
                }
                final IASTPreprocessorStatement[] ppstatements = translationUnit.getAllPreprocessorStatements();
                final FeatureParser featureParser = new FeatureParser();
                //actual tree building
                try {
                    featureParser.parseToTree(ppstatements, codelist.size(), fn);
                } catch (Exception e) {
                    System.err.println("error parsing to tree: "+file);
                    e.printStackTrace();
                }
                tree.addChild(fn);
            } else {
                final BinaryFileNode fn = new BinaryFileNode(tree, file);
                tree.addChild(fn);
            }
        }

        gitCommit.setTree(tree);
        //TODO: set changes another time --> later on when traversing the trees a second time
        //gitCommit.setChanges(committableChanges);
        //trigger listeners, etc.
        notifyObservers(gitCommit);
        System.out.println("commit nr.:"+this.size());
        return super.add(gitCommit);
    }

    //TODO: KEEP! may be useful later on
    private String removeNot(String s) {
        Pattern p = Pattern.compile("~ *\\((.*?)\\)");
        Matcher m = p.matcher(s);

        while (m.find())
            s = s.replaceFirst("~ *\\((.*?)\\)", m.group(1));

        return s;
    }

    /**
     * Helper method that prepares the given directory for further operation.
     * Used by all the autocommit methods.
     *
     * @param destDir the destination directory - this one will be committed
     * @param srcDir  the source directory
     * @param gitDir  the .git directory - will be deleted
     * @param gitSave the .git of the new repo that will be committed.
     *                will be saved every time before copying the new variant
     * @throws IOException
     */
    public void prepareDirectory(File destDir, File srcDir, File gitDir, File gitSave) throws IOException {
        boolean check = false;
        //save git folder
        for (File file : destDir.listFiles()) {
            if (file.getName().equals(".git")) {
                FileUtils.moveToDirectory(file.getAbsoluteFile(), gitSave, true);
                check = true;
                break;
            }
        }
        //delete old repo
        FileUtils.deleteDirectory(destDir);
        //copy new repo
        FileUtils.copyDirectory(srcDir, destDir);
        //delete copied git folder
        FileUtils.deleteDirectory(gitDir);
        //if there was a git folder in the beginning restore it.
        if (check) FileUtils.moveToDirectory(new File(gitSave.getPath()+"\\.git"), destDir, true);
    }


    private void notifyObservers(GitCommit gc) {
        for (GitCommitListener oc : observersC) {
            oc.onCommit(gc, this);
            /*for (GitCommitType gct : gc.getType()) {
                if (gct.equals(GitCommitType.BRANCH)) {
                    for (GitBranchListener ob : observersB) {
                        ob.onBranch(gc, this);
                    }
                }
                if (gct.equals(GitCommitType.MERGE)) {
                    for (GitMergeListener gm : observersM) {
                        gm.onMerge(gc, this);
                    }
                }
            }*/
        }
    }
}
