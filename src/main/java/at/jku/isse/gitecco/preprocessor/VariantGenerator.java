package at.jku.isse.gitecco.preprocessor;

import at.jku.isse.gitecco.conditionparser.ParsedCondition;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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

        File srcDir = new File(inPath);
        File destDir = new File(outPath);
        File gitDir = new File(outPath + "\\.git");

        try {
            FileUtils.deleteDirectory(destDir);
            FileUtils.copyDirectory(srcDir, destDir);
            FileUtils.deleteDirectory(gitDir);
        } catch (IOException e) {
            System.out.println("Failed to generate variants, copy of the og. dir failed.");
        }

        //set environment variable needs to be set for this to work!!
        String command = "coan source ";

        for (String s : conditions) {
            if (!command.contains("-D" + s + "=1"))
                command += "-D" + s + "=1 ";
        }

        command += "-m -ge -P --keepgoing --recurse --replace --filter c,h,cpp,cc,hpp,hh " + outPath;

        try {
            if(true)Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.out.println("Error in variant generation runtime command");
            e.printStackTrace();
        }

    }

}
