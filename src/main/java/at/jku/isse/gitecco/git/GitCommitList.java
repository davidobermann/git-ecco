package at.jku.isse.gitecco.git;

import at.jku.isse.gitecco.cdt.CDTHelper;
import at.jku.isse.gitecco.cdt.FeatureParser;
import at.jku.isse.gitecco.tree.BinaryFileNode;
import at.jku.isse.gitecco.tree.FileNode;
import at.jku.isse.gitecco.tree.RootNode;
import at.jku.isse.gitecco.tree.SourceFileNode;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.jgit.lib.Constants;

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

        gitHelper.checkOutCommit(gitCommit.getCommitName());

        if(self.size() >= 1) {
            final List<String> changedFiles = gitHelper.getChangedFiles(self.get(self.size()-1), gitCommit);

            for (String file : gitHelper.getRepositoryContents(gitCommit)) {
                final FileNode fn;

                if (FilenameUtils.getExtension(file).equals("cpp")
                        || FilenameUtils.getExtension(file).equals("c")
                        || FilenameUtils.getExtension(file).equals("h"))
                {

                    fn = new SourceFileNode(tree,file);
                    //check if source file has changed --> create subtree with features, otherwise insert src file as leaf
                    if(changedFiles.contains(file)) {
                        final String path = gitHelper.getPath() + "\\" + file;
                        final List<String> codelist = Files.readAllLines(Paths.get(path));
                        final String code = codelist.stream().collect(Collectors.joining("\n"));

                        final IASTTranslationUnit translationUnit = CDTHelper.parse(code.toCharArray());
                        final IASTPreprocessorStatement[] ppstatements = translationUnit.getAllPreprocessorStatements();
                        final FeatureParser featureParser = new FeatureParser();
                        //TODO: create tree parser which creates subtree for the passed node
                        featureParser.parseToTreeDefNew(ppstatements,codelist.size(),(SourceFileNode)fn);
                    }

                } else {
                    fn = new BinaryFileNode(tree,file);
                }

                tree.addChild(fn);

            }
        }

        gitCommit.setTree(tree);
        notifyObservers(gitCommit, self);
        return super.add(gitCommit);
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
