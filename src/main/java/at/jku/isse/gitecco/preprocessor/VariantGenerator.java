package at.jku.isse.gitecco.preprocessor;

import at.jku.isse.gitecco.conditionparser.ParsedCondition;

import java.io.IOException;
import java.util.Collection;

/**
 * Class for generating a variant of the repository.
 */
public class VariantGenerator {

    /**
     * generates a variant of the passed directory using the given features as defined values.
     * @param features the features which should be contained inside the variant.
     * @param inPath the ABSOLUTE path to the folder which should be processed to a variant.
     */
    public void generateVariants(Collection<ParsedCondition> features, String inPath) {
        //set environment variable needs to be set for this to work!!
        String command = "coan source ";

        for (ParsedCondition feature : features) {
            int value = (int) feature.getDefinition();
            if (!command.contains("-D" + feature.getName() + "=" + value))
                command += "-D" + feature.getName() + "=" + value + " ";
        }

        command += "-m -ge -P --filter c,h,cpp,cc,hpp,hh --keepgoing --recurse " + inPath;

        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.out.println("Error in variant generation runtime command");
            e.printStackTrace();
        }

    }

}
