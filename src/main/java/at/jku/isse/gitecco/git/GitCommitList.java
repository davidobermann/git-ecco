package at.jku.isse.gitecco.git;

import at.jku.isse.gitecco.cdt.CDTHelper;
import at.jku.isse.gitecco.cdt.FeatureParser;
import at.jku.isse.gitecco.tree.nodes.BinaryFileNode;
import at.jku.isse.gitecco.tree.nodes.FileNode;
import at.jku.isse.gitecco.tree.nodes.RootNode;
import at.jku.isse.gitecco.tree.nodes.SourceFileNode;
import at.jku.isse.gitecco.tree.visitor.GetAllChangedConditionsVisitor;
import at.jku.isse.gitecco.tree.visitor.LinkChangeVisitor;
import at.jku.isse.gitecco.tree.visitor.ValidateChangeVisitor;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public boolean add(GitCommit gitCommit) {
        try {
            throw new Exception("Not allowed to add unobserved");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * adds an element to the list.
     * also triggers the observers.
     *
     * @param gitCommit
     * @return
     */
    public boolean add(GitCommit gitCommit, GitCommitList self) throws Exception {
        final RootNode tree = new RootNode(gitHelper.getPath());
        List<String> changedFiles;
        Change[] changes;
        GitCommit oldCommit;
        gitHelper.checkOutCommit(gitCommit.getCommitName());

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

                //check if source file has changed --> create subtree with features, otherwise insert src file as leaf
                if (changedFiles.contains(file.replace("/","\\"))) {
                    final String path = gitHelper.getPath()+"\\"+file;
                    final List<String> codelist = Files.readAllLines(Paths.get(path));
                    final String code = codelist.stream().collect(Collectors.joining("\n"));

                    //file parsing
                    final IASTTranslationUnit translationUnit = CDTHelper.parse(code.toCharArray());
                    final IASTPreprocessorStatement[] ppstatements = translationUnit.getAllPreprocessorStatements();
                    final FeatureParser featureParser = new FeatureParser();
                    //actual tree building
                    featureParser.parseToTree(ppstatements, codelist.size(), (SourceFileNode) fn);

                    fn.setChanged();
                    //link changes
                    changes = gitHelper.getFileDiffs(gitCommit,file);
                    LinkChangeVisitor lcv = new LinkChangeVisitor();
                    //traverse tree for each change and mark changed features
                    for(Change change:changes) {
                        lcv.setChange(change);
                        fn.accept(lcv);
                    }
                    //corrects the ConditionBlockNodes if there children have changed.
                    fn.accept(new ValidateChangeVisitor());
                }
            } else {
                fn = new BinaryFileNode(tree, file);
                if(changedFiles.contains(file.replace("/","\\"))) fn.setChanged();
            }
            tree.addChild(fn);
        }
        gitCommit.setTree(tree);
        //trigger listeners, etc.
        notifyObservers(gitCommit, self);
        return super.add(gitCommit);
    }

    /**
     * Enables the auto commit mode:
     * creates a variant of the repository according to the changed conditions
     * and commits it to the ecco repository, the git repository and tracks the time for each process.
     */
    public void enableAutoCommitConfiguration() {
        this.addGitCommitListener(
                new GitCommitListener() {

                    private void checkAndCommit(GetAllChangedConditionsVisitor v) throws ParserException {
                        final FormulaFactory f = new FormulaFactory();
                        final PropositionalParser p = new PropositionalParser(f);
                        final Formula formula = p.parse(v.getAllConditionsConjuctive());
                        final SATSolver miniSat = MiniSat.miniSat(f);
                        miniSat.add(formula);
                        final Tristate result = miniSat.sat();

                        String commit = "ecco commit ";

                        if(result.equals(Tristate.TRUE)) {
                            Assignment model = miniSat.model();
                            for (Variable literal : model.positiveLiterals()) {
                                commit += literal;
                                if(v.getAllChangedConditions().contains(literal.toString())) {
                                    commit += "' ";
                                } else commit += " ";
                            }
                            System.out.println(commit);
                        } else {
                            //TODO: Not only changed, but also affected must be commited.
                            for (String condition : v.getAllChangedConditions()) {
                                checkAndCommitSingle(condition);
                            }
                        }
                    }

                    private void checkAndCommitSingle(String cond) throws ParserException {
                        final FormulaFactory f = new FormulaFactory();
                        final PropositionalParser p = new PropositionalParser(f);
                        final Formula formula = p.parse(cond);
                        final SATSolver miniSat = MiniSat.miniSat(f);
                        miniSat.add(formula);
                        final Tristate result = miniSat.sat();
                        String commit = "ecco commit ";
                        if(result.equals(Tristate.TRUE)) {
                            Assignment model = miniSat.model();
                            for (Variable literal : model.positiveLiterals()) {
                                commit += literal + "' ";
                            }
                            System.out.println(commit);
                        } else System.err.println("WARNING SINGLE CLAUSE IS NOT SATISFIABLE!");
                    }

                    @Override
                    public void onCommit(GitCommit gc, GitCommitList gcl) {

                        System.out.println("---------------------------------");

                        GetAllChangedConditionsVisitor v = new GetAllChangedConditionsVisitor();
                        gc.getTree().accept(v);

                        try {
                            checkAndCommit(v);
                        } catch (ParserException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }


    private void notifyObservers(GitCommit gc, GitCommitList self) {
        for (GitCommitListener oc : observersC) {
            oc.onCommit(gc, self);
            for (GitCommitType gct : gc.getType()) {
                if (gct.equals(GitCommitType.BRANCH)) {
                    for (GitBranchListener ob : observersB) {
                        ob.onBranch(gc, self);
                    }
                }
                if (gct.equals(GitCommitType.MERGE)) {
                    for (GitMergeListener gm : observersM) {
                        gm.onMerge(gc, self);
                    }
                }
            }
        }
    }
}
