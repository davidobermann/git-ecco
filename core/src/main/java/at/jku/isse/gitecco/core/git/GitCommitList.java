package at.jku.isse.gitecco.core.git;

import at.jku.isse.ecco.EccoService;
import at.jku.isse.ecco.core.Association;
import at.jku.isse.gitecco.core.cdt.CDTHelper;
import at.jku.isse.gitecco.core.cdt.FeatureParser;
import at.jku.isse.gitecco.core.tree.nodes.BinaryFileNode;
import at.jku.isse.gitecco.core.tree.nodes.FileNode;
import at.jku.isse.gitecco.core.tree.nodes.RootNode;
import at.jku.isse.gitecco.core.tree.nodes.SourceFileNode;
import at.jku.isse.gitecco.core.tree.util.ChangeComputation;
import at.jku.isse.gitecco.core.tree.util.ComittableChange;
import com.opencsv.CSVWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class for storing a row of commits
 * With the option to react to added commits
 * depending on their type.
 */
public class GitCommitList extends ArrayList<GitCommit> {
    private final GitHelper gitHelper;
    private final List<GitCommitListener> observersC = new ArrayList();
    private final List<GitBranchListener> observersB = new ArrayList();
    private final List<GitMergeListener> observersM = new ArrayList();
    private boolean dispose = false;

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
     * Disposes the tree of the commit after evaluation.
     * HIGHLY RECOMMENDED FOR BIG REPOSITORIES
     */
    public void setTreeDispose(boolean enable) {
        this.dispose = enable;
    }

    /**
     * Dummy method which blocks unobserved adding.
     * @param gitCommit
     * @return
     */
    @Override
    public boolean add(GitCommit gitCommit) {
        final RootNode tree = new RootNode(gitHelper.getPath());
        List<String> changedFiles;
        Change[] changes;
        final List<ComittableChange> committableChanges = new ArrayList<>();
        GitCommit oldCommit;
        gitHelper.checkOutCommit(gitCommit.getCommitName());

        try {
            changedFiles = gitHelper.getChangedFiles(gitCommit);
            //for all files
            for (String file : gitHelper.getRepositoryContents(gitCommit)) {
                final FileNode fn;

                //source file or binary file --> with parsing, without.
                if (FilenameUtils.getExtension(file).equals("cpp")
                        || FilenameUtils.getExtension(file).equals("c")
                        || FilenameUtils.getExtension(file).equals("h")
                        || FilenameUtils.getExtension(file).equals("hpp")
                        || FilenameUtils.getExtension(file).equals("hh")) {

                    fn = new SourceFileNode(tree, file);

                    //build tree for every file.
                    final String path = gitHelper.getPath()+"\\"+file;

                    List<String> codelist = Files.readAllLines(Paths.get(path), StandardCharsets.ISO_8859_1);
                    final String code = codelist.stream().collect(Collectors.joining("\n"));

                    //file parsing
                    final IASTTranslationUnit translationUnit = CDTHelper.parse(code.toCharArray());
                    final IASTPreprocessorStatement[] ppstatements = translationUnit.getAllPreprocessorStatements();
                    final FeatureParser featureParser = new FeatureParser();
                    //actual tree building
                    featureParser.parseToTree(ppstatements, codelist.size(), (SourceFileNode) fn);

                } else {
                    fn = new BinaryFileNode(tree, file);
                    if(changedFiles.contains(file.replace("/","\\"))) fn.setChanged();
                }
                tree.addChild(fn);
            }
        } catch (Exception e) {
            System.err.println("error while adding a git commit to the commit list");
            e.printStackTrace();
        }

        gitCommit.setTree(tree);
        gitCommit.setChanges(committableChanges);
        //trigger listeners, etc.
        notifyObservers(gitCommit);
        System.out.println("commit nr.:" + this.size());
        return super.add(gitCommit);
    }

    //TODO: KEEP! may be useful later on
    private String removeNot(String s) {
        Pattern p = Pattern.compile("~ *\\((.*?)\\)");
        Matcher m = p.matcher(s);

        while(m.find())
            s = s.replaceFirst("~ *\\((.*?)\\)",m.group(1));

        return s;
    }

    /**
     * Helper method that prepares the given directory for further operation.
     * Used by all the autocommit methods.
     * @param destDir the destination directory - this one will be committed
     * @param srcDir the source directory
     * @param gitDir the .git directory - will be deleted
     * @param gitSave the .git of the new repo that will be committed.
     *               will be saved every time before copying the new variant
     * @throws IOException
     */
    public void prepareDirectory(File destDir, File srcDir, File gitDir, File gitSave) throws IOException {
        boolean check = false;
        //save git folder
        for (File file : destDir.listFiles()) {
            if(file.getName().equals(".git")) {
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
        if(check) FileUtils.moveToDirectory(new File(gitSave.getPath() + "\\.git"), destDir, true);
    }



    private void notifyObservers(GitCommit gc) {
        for (GitCommitListener oc : observersC) {
            oc.onCommit(gc, this);
            for (GitCommitType gct : gc.getType()) {
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
            }
        }
    }
}
