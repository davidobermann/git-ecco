package at.jku.isse.gitecco.preprocessor;

import at.jku.isse.gitecco.cdt.TreeFeature;
import at.jku.isse.gitecco.conditionparser.ParsedCondition;
import at.jku.isse.gitecco.git.GitHelper;

import java.util.Collection;
import java.util.List;

public class VariantGenerator {

    public void generateVariants(Collection<ParsedCondition> features, String inPath, String outPath) {
        String command = "coan spin "; //spin: replicates the old folder structure into a new folder structure

        for (ParsedCondition feature : features) {
            int value = (int) feature.getDefinition();
            if (!command.contains("-D" + feature.getName() + "=" + value))
                command += "-D" + feature.getName() + "=" + value + " ";
        }

        //command += "-m -ge -P -r " + file;

    }

    public void preprocess(TreeFeature tree, String file) {
        List<TreeFeature> changed = tree.getChangedAsList();
        String command = "coan source ";
        for (TreeFeature tf : changed) {
            for (ParsedCondition pc : tf.getConditions()) {
                int value = (int) pc.getDefinition();
                if (!command.contains("-D"+pc.getName()+"="+value)) {
                    command += "-D"+pc.getName()+"="+value+" ";
                }
            }
        }
        command += "-m -ge -P -r "+file;
        System.out.println(command);
    }

}
