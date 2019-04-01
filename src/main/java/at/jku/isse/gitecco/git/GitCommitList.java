package at.jku.isse.gitecco.git;

import at.jku.isse.ecco.EccoService;
import at.jku.isse.ecco.core.Association;
import at.jku.isse.gitecco.cdt.CDTHelper;
import at.jku.isse.gitecco.cdt.FeatureParser;
import at.jku.isse.gitecco.preprocessor.VariantGenerator;
import at.jku.isse.gitecco.tree.nodes.BinaryFileNode;
import at.jku.isse.gitecco.tree.nodes.FileNode;
import at.jku.isse.gitecco.tree.nodes.RootNode;
import at.jku.isse.gitecco.tree.nodes.SourceFileNode;
import at.jku.isse.gitecco.tree.util.ChangeComputation;
import at.jku.isse.gitecco.tree.util.ComittableChange;
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
        final List<ComittableChange> committableChanges = new ArrayList<>();
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
                //if (changedFiles.contains(file.replace("/","\\"))) {

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

                fn.setChanged();
                //link changes: commit of monday 21st of jan. --> linkChanges still in.
                changes = gitHelper.getFileDiffs(gitCommit, file);

                //get the nodes, etc, for the commit
                ChangeComputation cp = new ChangeComputation();
                committableChanges.addAll(cp.getChanged());

                //add each of them to the list --> list is later linked to the commit.
                for (Change c : changes) {
                    cp.computeForChange(c, (SourceFileNode) fn);
                    committableChanges.addAll(cp.getChanged());
                }

                //}
            } else {
                fn = new BinaryFileNode(tree, file);
                if(changedFiles.contains(file.replace("/","\\"))) fn.setChanged();
            }
            tree.addChild(fn);
        }
        gitCommit.setTree(tree);
        gitCommit.setChanges(committableChanges);
        //trigger listeners, etc.
        notifyObservers(gitCommit, self);
        System.out.println("commit nr.:" + this.size());
        return super.add(gitCommit);
    }

    /**
     * Enables the autoCommitMode.
     * This will trigger for each added commit.
     * For each commit the repository will be copied to another place (gitrepo folder) and committed.
     * Also for each chane of the commit an ecco commit for a generated variant will be performed.
     * For those 2 processes time will be measured and printed to a csv file.
     */
    public void enableAutoCommitConfig() {
        this.addGitCommitListener(
                (gc, gcl) -> {
                    final Set<String> config = new HashSet<>();
                    String commitConfig = "";
                    String baseFolder = gitHelper.getPath().substring(0,gitHelper.getPath().lastIndexOf('\\'));
                    String gitrepo = baseFolder + "\\gitrepo";
                    String eccorepo = baseFolder + "\\eccorepo";

                    //GIT
                    File srcDir = new File(gitHelper.getPath());
                    File destDir = new File(gitrepo);
                    File gitDir = new File(gitrepo + "\\.git");
                    File gitSave = new File(baseFolder);
                    Git git = null;
                    GitCommand c = null;
                    boolean append = true;

                    try {
                        prepareDirectory(destDir,srcDir, gitDir, gitSave);
                        if(gcl.size() < 1) {
                            //safety delete of git folder
                            FileUtils.deleteDirectory(gitDir);
                            git = Git.init().setDirectory(destDir).call();
                            append = false;
                        } else {
                            git = Git.open(destDir);
                        }

                        c = git.commit().setMessage("");
                        git.add().addFilepattern(".").call();

                    } catch (IOException|GitAPIException e) {
                        System.out.println("Failed to generate variants, copy of the og. dir failed.");
                    }

                    long gitTime = System.currentTimeMillis();

                    try {
                        c.call();
                    } catch (GitAPIException e) {
                        System.out.println("Commit failed!");
                        e.printStackTrace();
                    }

                    System.out.println("------\ngit commit: " + (gcl.size() + 1));
                    gitTime = System.currentTimeMillis() - gitTime;

                    git.close();


                    //ECCO + CSV Output
                    long eccoTime = 0;
                    String doubleCheck = "";
                    final File csvFile = new File(gitHelper.getPath()+"result.csv");
                    FileWriter outputfile = null;
                    try { outputfile = new FileWriter(csvFile, append); } catch(IOException ioe){
                        System.out.println("Error while handling the csv file output!");
                    }

                    // create CSVWriter object filewriter object as parameter
                    //deprcated but no other way available --> it still works anyways
                    @SuppressWarnings("deprecation") CSVWriter writer = new CSVWriter(outputfile, ';', CSVWriter.NO_QUOTE_CHARACTER);

                    //if this is the first commit also add the header.
                    if(gcl.size() < 1) {
                        // adding header to csv
                        String[] header = {"CommitNr", "Commit-Hash", "GitTime[ms]", "EccoTime[ms]"};
                        writer.writeNext(header);

                        File ecco = new File(eccorepo + "\\repo");
                        File eccodata = new File(eccorepo + "\\data");
                        File eccodel = new File(eccorepo + "\\repo\\.ecco");
                        try {
                            if(eccodel.exists()) FileUtils.deleteDirectory(eccodel);
                            if(!ecco.exists()) FileUtils.forceMkdir(ecco);
                            if(!eccodata.exists()) FileUtils.forceMkdir(eccodata);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    EccoService eccoService =
                            new EccoService(Paths.get(eccorepo + "\\data"),Paths.get(eccorepo + "\\repo\\.ecco"));
                    if(gcl.size() < 1) eccoService.init();
                    else eccoService.open();

                    //for every changed cond. --> one partial ecco commit:
                    for (ComittableChange change : gc.getChanges()) {
                        //Create commit config:
                        final List<String> configList = extractChangedLiterals(change.getChanged());
                        configList.addAll(change.getAffected());
                        commitConfig = configList.stream().filter(s -> s.length()>=1).collect(Collectors.joining(","));

                        //if this partial ecco commit was already performed for this git commit --> skip the rest.
                        if(doubleCheck.contains(commitConfig)) continue;
                        doubleCheck += commitConfig;

                        //create variant config
                        config.add(change.getChanged());
                        config.addAll(change.getAffected());

                        //generate variant: (also moves to the expected directory)
                        VariantGenerator vg = new VariantGenerator();
                        vg.generateVariants(config,gitHelper.getPath(),eccorepo);

                        System.out.println("ecco commit: " + commitConfig);

                        eccoTime = System.currentTimeMillis();
                        eccoService.commit(commitConfig);
                        eccoTime = System.currentTimeMillis() - eccoTime;

                        // add data to csv
                        String[] data = {String.valueOf(gcl.size()), gc.getCommitName(),
                                String.valueOf(gitTime), String.valueOf(eccoTime)};

                        writer.writeNext(data);
                    }

                    eccoService.close();

                    // closing writer connection
                    try { writer.close(); } catch (IOException e) { e.printStackTrace(); }

                    if(dispose) gc.disposeTree();
                }
        );
    }

    /**
     * Same as enableAutoCommitConfig
     * Except ecco commits will be performed for every possible model of a changed condition.
     */
    public void enableAutoCommitConfigForEveryModel(){
        this.addGitCommitListener(
                (gc, gcl) -> {
                    final Set<String> config = new HashSet<>();
                    String commitConfig = "";
                    String baseFolder = gitHelper.getPath().substring(0,gitHelper.getPath().lastIndexOf('\\'));
                    String gitrepo = baseFolder + "\\gitrepo";
                    String eccorepo = baseFolder + "\\eccorepo";

                    //GIT
                    File srcDir = new File(gitHelper.getPath());
                    File destDir = new File(gitrepo);
                    File gitDir = new File(gitrepo + "\\.git");
                    File gitSave = new File(baseFolder);
                    Git git = null;
                    GitCommand c = null;
                    boolean append = true;

                    try {
                        prepareDirectory(destDir,srcDir, gitDir, gitSave);
                        if(gcl.size() < 1) {
                            //safety delete of git folder
                            FileUtils.deleteDirectory(gitDir);
                            git = Git.init().setDirectory(destDir).call();
                            append = false;
                        } else {
                            git = Git.open(destDir);
                        }

                        c = git.commit().setMessage("");
                        git.add().addFilepattern(".").call();

                    } catch (IOException|GitAPIException e) {
                        System.out.println("Failed to generate variants, copy of the og. dir failed.");
                    }

                    long gitTime = System.currentTimeMillis();

                    try {
                        c.call();
                    } catch (GitAPIException e) {
                        System.out.println("Commit failed!");
                        e.printStackTrace();
                    }

                    System.out.println("------\ngit commit: " + (gcl.size() + 1));
                    gitTime = System.currentTimeMillis() - gitTime;

                    git.close();


                    //ECCO + CSV Output
                    long eccoTime = 0;
                    String doubleCheck = "";
                    final File csvFile = new File(gitHelper.getPath()+"result.csv");
                    FileWriter outputfile = null;
                    try { outputfile = new FileWriter(csvFile, append); } catch(IOException ioe){
                        System.out.println("Error while handling the csv file output!");
                    }

                    // create CSVWriter object filewriter object as parameter
                    //deprcated but no other way available --> it still works anyways
                    @SuppressWarnings("deprecation") CSVWriter writer = new CSVWriter(outputfile, ';', CSVWriter.NO_QUOTE_CHARACTER);

                    //if this is the first commit also add the header.
                    if(gcl.size() < 1) {
                        // adding header to csv
                        String[] header = {"CommitNr", "Commit-Hash", "GitTime[ms]", "EccoTime[ms]"};
                        writer.writeNext(header);

                        File ecco = new File(eccorepo + "\\repo");
                        File eccodata = new File(eccorepo + "\\data");
                        File eccodel = new File(eccorepo + "\\repo\\.ecco");
                        try {
                            if(eccodel.exists()) FileUtils.deleteDirectory(eccodel);
                            if(!ecco.exists()) FileUtils.forceMkdir(ecco);
                            if(!eccodata.exists()) FileUtils.forceMkdir(eccodata);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    EccoService eccoService =
                            new EccoService(Paths.get(eccorepo + "\\data"),Paths.get(eccorepo + "\\repo\\.ecco"));
                    if(gcl.size() < 1) eccoService.init();
                    else eccoService.open();

                    //for every changed cond. --> one partial ecco commit:
                    for (ComittableChange change : gc.getChanges()) {
                        //Create commit config:
                        for (List<String> configList : extractChangedLiteralsForAllModels(change.getChanged())) {
                            configList.addAll(change.getAffected());
                            commitConfig = configList.stream().filter(s -> s.length()>=1).collect(Collectors.joining(","));

                            //if this partial ecco commit was already performed for this git commit --> skip the rest.
                            if(doubleCheck.contains(commitConfig)) continue;
                            doubleCheck += commitConfig;

                            //create variant config
                            config.add(change.getChanged());
                            config.addAll(change.getAffected());

                            //generate variant: (also moves to the expected directory)
                            VariantGenerator vg = new VariantGenerator();
                            vg.generateVariants(config,gitHelper.getPath(),eccorepo);

                            System.out.println("ecco commit: " + commitConfig);

                            //TODO: remove try catch bock for accurate time measurement.
                            eccoTime = System.currentTimeMillis();
                            /*try {

                            } catch (EccoException ee) {
                                System.out.println("error in ecco commit --> ignore for now");
                                eccoTime = -100;
                            }*/
                            eccoService.commit(commitConfig);
                            eccoTime = System.currentTimeMillis() - eccoTime;

                            // add data to csv
                            String[] data = {String.valueOf(gcl.size()), gc.getCommitName(),
                                    String.valueOf(gitTime), String.valueOf(eccoTime)};

                            writer.writeNext(data);
                        }
                    }

                    eccoService.close();

                    // closing writer connection
                    try { writer.close(); } catch (IOException e) { e.printStackTrace(); }

                    if(dispose) gc.disposeTree();
                }
        );
    }

    /**
     * Same as enableAutoCommitConfigForEveryModel
     * Except that here also the amount of artifacts + the amount of features will be counted.
     */
    public void enableAutoCommitConfigForEveryModelAndCntArtifacts(){
        this.addGitCommitListener(
                (gc, gcl) -> {
                    final Set<String> config = new HashSet<>();
                    String commitConfig = "";
                    String baseFolder = gitHelper.getPath().substring(0,gitHelper.getPath().lastIndexOf('\\'));
                    String gitrepo = baseFolder + "\\gitrepo";
                    String eccorepo = baseFolder + "\\eccorepo";

                    //GIT
                    File srcDir = new File(gitHelper.getPath());
                    File destDir = new File(gitrepo);
                    File gitDir = new File(gitrepo + "\\.git");
                    File gitSave = new File(baseFolder);
                    Git git = null;
                    GitCommand c = null;
                    boolean append = true;

                    try {
                        prepareDirectory(destDir,srcDir, gitDir, gitSave);
                        if(gcl.size() < 1) {
                            //safety delete of git folder
                            FileUtils.deleteDirectory(gitDir);
                            git = Git.init().setDirectory(destDir).call();
                            append = false;
                        } else {
                            git = Git.open(destDir);
                        }

                        c = git.commit().setMessage("");
                        git.add().addFilepattern(".").call();

                    } catch (IOException|GitAPIException e) {
                        System.out.println("Failed to generate variants, copy of the og. dir failed.");
                    }

                    long gitTime = System.currentTimeMillis();

                    try {
                        c.call();
                    } catch (GitAPIException e) {
                        System.out.println("Commit failed!");
                        e.printStackTrace();
                    }

                    System.out.println("------\ngit commit: " + (gcl.size() + 1));
                    gitTime = System.currentTimeMillis() - gitTime;

                    git.close();


                    //ECCO + CSV Output
                    long eccoTime = 0;
                    String doubleCheck = "";
                    final File csvFile = new File(gitHelper.getPath()+"result.csv");
                    FileWriter outputfile = null;
                    try { outputfile = new FileWriter(csvFile, append); } catch(IOException ioe){
                        System.out.println("Error while handling the csv file output!");
                    }

                    // create CSVWriter object filewriter object as parameter
                    //deprcated but no other way available --> it still works anyways
                    @SuppressWarnings("deprecation") CSVWriter writer = new CSVWriter(outputfile, ';', CSVWriter.NO_QUOTE_CHARACTER);

                    //if this is the first commit also add the header.
                    if(gcl.size() < 1) {
                        // adding header to csv
                        String[] header = {"CommitNr", "Commit-Hash", "GitTime[ms]",
                                "EccoTime[ms]", "Features", "Artifacts"};

                        writer.writeNext(header);

                        File ecco = new File(eccorepo + "\\repo");
                        File eccodata = new File(eccorepo + "\\data");
                        File eccodel = new File(eccorepo + "\\repo\\.ecco");
                        try {
                            if(eccodel.exists()) FileUtils.deleteDirectory(eccodel);
                            if(!ecco.exists()) FileUtils.forceMkdir(ecco);
                            if(!eccodata.exists()) FileUtils.forceMkdir(eccodata);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    EccoService eccoService =
                            new EccoService(Paths.get(eccorepo + "\\data"),Paths.get(eccorepo + "\\repo\\.ecco"));
                    if(gcl.size() < 1) eccoService.init();
                    else eccoService.open();

                    //for every changed cond. --> one partial ecco commit:
                    for (ComittableChange change : gc.getChanges()) {
                        //Create commit config:
                        for (List<String> configList : extractChangedLiteralsForAllModels(change.getChanged())) {
                            configList.addAll(change.getAffected());
                            commitConfig = configList.stream().filter(s -> s.length()>=1).collect(Collectors.joining(","));

                            //if this partial ecco commit was already performed for this git commit --> skip the rest.
                            if(doubleCheck.contains(commitConfig)) continue;
                            doubleCheck += commitConfig;
                            commitConfig = removeNot(commitConfig);

                            //create variant config
                            config.add(change.getChanged());
                            config.addAll(change.getAffected());

                            //generate variant: (also moves to the expected directory)
                            VariantGenerator vg = new VariantGenerator();
                            vg.generateVariants(config,gitHelper.getPath(),eccorepo);

                            System.out.println("ecco commit: " + commitConfig);

                            eccoTime = System.currentTimeMillis();
                            eccoService.commit(commitConfig);
                            eccoTime = System.currentTimeMillis() - eccoTime;

                            //Count all the artifacts of the current state of the repository
                            int artifactCnt = 0;
                            for (Association a : eccoService.getRepository().getAssociations()) {
                                artifactCnt += a.getRootNode().countArtifacts();
                            }

                            //Count all the tracked features.
                            int featureCnt = eccoService.getRepository().getFeatures().size();

                            // add data to csv
                            String[] data = {String.valueOf(gcl.size()), gc.getCommitName(),
                                    String.valueOf(gitTime), String.valueOf(eccoTime),
                                    String.valueOf(featureCnt), String.valueOf(artifactCnt)};

                            writer.writeNext(data);
                        }
                    }

                    eccoService.close();

                    // closing writer connection
                    try { writer.close(); } catch (IOException e) { e.printStackTrace(); }

                    if(dispose) gc.disposeTree();
                }
        );
    }

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
     * @param destDir
     * @param srcDir
     * @param gitDir
     * @param gitSave
     * @throws IOException
     */
    private void prepareDirectory(File destDir, File srcDir, File gitDir, File gitSave) throws IOException {
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

    /**
     * Extracts all the changed literals form a condition.
     * Also every disjunction is translated into a conjunction so every positive literal can be obtained.
     * @param s
     * @return
     */
    private List<List<String>> extractChangedLiteralsForAllModels(String s) {
        final List<List<String>> ret = new ArrayList();
        List<String> retList;
        try {
            final FormulaFactory f = new FormulaFactory();
            final PropositionalParser p = new PropositionalParser(f);
            //also turn disjunctive clauses into conjunctive clauses to get every positive literal
            s = s.replace('!','~').replace("&&","&").replace("||","&");
            final Formula formula = p.parse(s);
            final SATSolver miniSat = MiniSat.miniSat(f);
            miniSat.add(formula);
            final Tristate result = miniSat.sat();

            List<Assignment> allModels = miniSat.enumerateAllModels();

            for (Assignment assignment : allModels) {
                retList = new ArrayList<>();
                for (Variable positiveLiteral : assignment.positiveLiterals()) {
                    retList.add(positiveLiteral.toString() + "'");
                }
                ret.add(retList);
            }

        } catch (ParserException e) {
            System.out.println("Error while extracting literals of condition");
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Extracts all the changed literals form a condition.
     * Also every disjunction is translated into a conjunction so every positive literal can be obtained.
     * @param s
     * @return
     */
    private List<String> extractChangedLiterals(String s) {
        final List<String> ret = new ArrayList<>();
        try {
            final FormulaFactory f = new FormulaFactory();
            final PropositionalParser p = new PropositionalParser(f);
            s = s.replace('!','~').replace("&&","&").replace("||","|");
            final Formula formula = p.parse(s);
            final SATSolver miniSat = MiniSat.miniSat(f);
            miniSat.add(formula);
            final Tristate result = miniSat.sat();

            Assignment model = miniSat.model();

            //List<Assignment> allModels = miniSat.enumerateAllModels();

            for (Variable positiveLiteral : model.positiveLiterals()) {
                ret.add(positiveLiteral.toString() + "'");
            }

        } catch (ParserException e) {
            System.out.println("Error while extracting literals of condition");
            e.printStackTrace();
        }

        return ret;
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
