package at.jku.isse.gitecco.translation;

import at.jku.isse.gitecco.core.git.Change;
import at.jku.isse.gitecco.core.git.GitCommitList;
import at.jku.isse.gitecco.core.git.GitHelper;
import at.jku.isse.gitecco.core.preprocessor.PreprocessorHelper;
import at.jku.isse.gitecco.core.solver.ExpressionSolver;
import at.jku.isse.gitecco.core.tree.nodes.ConditionalNode;
import at.jku.isse.gitecco.core.tree.nodes.FileNode;
import at.jku.isse.gitecco.core.tree.nodes.SourceFileNode;
import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.translation.constraintcomputation.util.GetNodesForChangeVisitor;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App {

	public static void main(String... args) throws Exception{
	    final boolean debug = true;
	    //TODO: planned arguments: DEBUG, dispose tree, max commits, repo path, csv path(feature id), outpath for ecco
        String repoPath = "C:\\obermanndavid\\git-ecco-test\\appimpleTest\\marlin\\Marlin";

        final GitHelper gitHelper = new GitHelper(repoPath);
        final GitCommitList commitList = new GitCommitList(repoPath);

        commitList.addGitCommitListener((gc,gcl)-> {

            //TODO: do the git commit and measure time or whatever

            GetNodesForChangeVisitor visitor = new GetNodesForChangeVisitor();
            Set<ConditionalNode> changedNodes = new HashSet<>();
            List<String> changedFiles = gitHelper.getChangedFiles(gc);

            //retrieve changed nodes
            for (FileNode child : gc.getTree().getChildren()) {
                if(child instanceof SourceFileNode && changedFiles.contains(child.getFilePath().replace("/","\\"))) {
                    Change[] changes = null;
                    try {
                        changes = gitHelper.getFileDiffs(gc, child);
                    } catch (Exception e) {
                        System.err.println("error while executing the file diff: " + child.getFilePath());
                        e.printStackTrace();
                    }

                    for (Change change : changes) {
                        visitor.setChange(change);
                        child.accept(visitor);
                        changedNodes.addAll(visitor.getchangedNodes());
                    }
                }
            }

            //compute assignment for preprocessing and generate variants
            ExpressionSolver solver = new ExpressionSolver();
            PreprocessorHelper pph = new PreprocessorHelper();
            final File gitFolder = new File(gitHelper.getPath());
            final File eccoFolder = new File(gitFolder.getParent(), "ecco");

            //for each changed node:
            for (ConditionalNode changedNode : changedNodes) {
                //TODO: previous constraints and affected constraints
                solver.setExpr(changedNode.getCondition());
                Map<Feature, Integer> result = solver.solve();
                solver.reset();
                pph.generateVariants(result, gitFolder, eccoFolder);
                System.out.println("CONFIG FOR PREPROCESSING:");
                result.entrySet().forEach(x->System.out.print(x.getKey() + " = " + x.getValue() + "; "));

                //TODO: ecco commit with solution + marked as changed
            }

        });

        gitHelper.getAllCommits(commitList);

    }

}
