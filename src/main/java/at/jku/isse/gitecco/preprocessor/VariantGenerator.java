package at.jku.isse.gitecco.preprocessor;

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
import java.util.Collection;

/**
 * Class for generating a variant of the repository.
 */
public class VariantGenerator {
    /**
     * generates a variant of the passed directory using the given features as defined values.
     * @param conditions the features which should be contained inside the variant.
     * @param inPath the ABSOLUTE path to the folder which should be processed to a variant.
     */
    public void generateVariants(Collection<String> conditions, String inPath, String outPath) {

        //Test for dead code:
        String test = "";
        for (String condition : conditions) {
            test += condition.replace('!','~').replace("&&","&").replace("||","|");
        }

        try {
            final FormulaFactory f = new FormulaFactory();
            final PropositionalParser p = new PropositionalParser(f);
            final Formula formula = p.parse(test);
            final SATSolver miniSat = MiniSat.miniSat(f);
            miniSat.add(formula);
            final Tristate result = miniSat.sat();

            if(!result.equals(Tristate.TRUE)) throw new InvalidParameterException("Dead Code!");

        } catch (ParserException e) {
            e.printStackTrace();
        }

        File srcDir = new File(inPath);
        File destDir = new File(outPath);
        File gitDir = new File(outPath + "\\.git");
        File eccoSave = new File(outPath.substring(0,outPath.lastIndexOf('\\')));

        try {
            prepareDirectory(destDir, srcDir, gitDir, eccoSave);
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
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.out.println("Error in variant generation runtime command");
            e.printStackTrace();
        }

    }

    /**
     * prepares the directory for the following operations:
     * if the .ecco folder exists it is moved to a save location.
     * after that the entire folder is deleted and will be replaced by the git repo.
     * the saved .ecco folder is then moved back into the eccorepo and the .git will be delted.
     * @param destDir
     * @param srcDir
     * @param gitDir
     * @param eccoSave
     * @throws IOException
     */
    private void prepareDirectory(File destDir, File srcDir, File gitDir, File eccoSave) throws IOException {
        boolean check = false;
        for (File file : destDir.listFiles()) {
            if (file.getName().equals(".ecco")) {
                FileUtils.moveToDirectory(file.getAbsoluteFile(), eccoSave, true);
                check = true;
                break;
            }
        }
        FileUtils.deleteDirectory(destDir);
        FileUtils.copyDirectory(srcDir, destDir);
        if (check) FileUtils.moveToDirectory(new File(eccoSave.getPath()+"\\.ecco"), destDir, true);
        FileUtils.deleteDirectory(gitDir);
    }
}
