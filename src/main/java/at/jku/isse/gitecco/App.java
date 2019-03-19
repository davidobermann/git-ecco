package at.jku.isse.gitecco;

import at.jku.isse.gitecco.git.GitCommitList;
import at.jku.isse.gitecco.git.GitHelper;
import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.io.parsers.PseudoBooleanParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;
import org.logicng.solvers.sat.MiniCard;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main app for the git to ecco tool.
 */
public class App {

    /**
     * Main method.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        //final String repositoryPath = "C:\\obermanndavid\\git-to-ecco\\test_repo2";
        //old repo: not suitable anymore since we assume there will be only true/false expressions.
        final String repositoryPath = "C:\\obermanndavid\\git-to-ecco\\test_repo3";
        //final String repositoryPath = "C:\\obermanndavid\\git-ecco-test\\test1\\Unity";

        //final String repositoryPath = "C:\\obermanndavid\\git-ecco-test\\test2\\betaflight";
        //final String repositoryPath = "C:\\obermanndavid\\git-to-ecco\\test_repo4";
        final GitHelper gitHelper = new GitHelper(repositoryPath);
        final GitCommitList commits = new GitCommitList(repositoryPath);
        commits.enableAutoCommitConfigForEveryModelAndCntArtifacts();
        gitHelper.getAllCommits(commits);

        /*final String path = "C:\\obermanndavid\\git-ecco-test\\test2\\betaflight\\drv_bmp085.c";
        List<String> codelist = Files.readAllLines(Paths.get(path), StandardCharsets.ISO_8859_1);

        codelist.forEach(System.out::println);*/

        /*String condition = "not defined(TRUSTED_ACCZ)";

        condition = removeDefinedMacro(condition);
        final FormulaFactory f = new FormulaFactory();
        final PropositionalParser p = new PropositionalParser(f);
        condition = condition.replace('!', '~').replace("&&", "&").replace("||", "|");
        final Formula formula = p.parse(condition);
        final SATSolver miniSat = MiniSat.miniSat(f);
        miniSat.add(formula);
        final Tristate result = miniSat.sat();
        Assignment model = miniSat.model();
        model.positiveLiterals().forEach(System.out::println);
        System.out.println(condition);*/
    }

    private static String removeDefinedMacro(String s) {
        Pattern p = Pattern.compile("defined *\\((.*?)\\)");
        Matcher m = p.matcher(s);

        while(m.find())
            s = s.replaceFirst("defined *\\((.*?)\\)", m.group(1));

        return s.replace("defined","").replace("not","!");
    }

}
