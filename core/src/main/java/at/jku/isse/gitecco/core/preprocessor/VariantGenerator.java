package at.jku.isse.gitecco.core.preprocessor;

import org.apache.commons.io.FileUtils;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Set;

import static org.eclipse.collections.impl.block.factory.StringPredicates.matches;

/**
 * Class for generating a variant of the repository.
 */
public class VariantGenerator {
    /**
     * generates a variant of the passed directory using the given features as defined values.
     * @param conditions the features which should be contained inside the variant.
     * @param inPath the ABSOLUTE path to the folder which should be processed to a variant.
     */
    public void generateVariants(Set<String> conditions, String inPath, String outPath) {

        //build condition to test for dead code:
        String test = "";
        boolean first = true;
        for (String condition : conditions) {
            if(first || condition.matches(".*\\w.*")) {
                test = condition.replace('!','~').replace("&&","&").replace("||","|");
                first = false;
            } else {
                test += " & " + condition.replace('!','~').replace("&&","&").replace("||","|");
            }
        }

        //check for dead code
        try {
            final FormulaFactory f = new FormulaFactory();
            final PropositionalParser p = new PropositionalParser(f);
            final Formula formula = p.parse(test);
            final SATSolver miniSat = MiniSat.miniSat(f);
            miniSat.add(formula);
            final Tristate result = miniSat.sat();

            if(!result.equals(Tristate.TRUE)) throw new InvalidParameterException("Dead Code!");

        } catch (ParserException e) {
            System.out.println("error in variant generation: " + test);
            e.printStackTrace();
        }

        File srcDir = new File(inPath);
        File destDir = new File(outPath + "\\data");
        File gitDir = new File(outPath + "\\data\\.git");

        try {
            prepareDirectory(destDir, srcDir, gitDir);
        } catch (IOException e) {
            System.out.println("Failed to generate variants, copy of the og. dir failed.");
        }

        //set environment variable needs to be set for this to work!!
        String command = "coan source ";

        for (String s : conditions) {
            if (!command.contains("-D" + s + "=1")) command += "-D" + s + "=1 ";
        }

        command += "-m -ge -P --keepgoing --recurse --replace --filter c,h,cpp,cc,hpp,hh " + outPath;

        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
        } catch (IOException|InterruptedException e) {
            System.out.println("Error in variant generation runtime command");
            e.printStackTrace();
        }

    }

    private void prepareDirectory(File destDir, File srcDir, File gitDir) throws IOException {
        FileUtils.deleteDirectory(destDir);
        FileUtils.copyDirectory(srcDir, destDir);
        FileUtils.deleteDirectory(gitDir);
    }
}
