package at.jku.isse.gitecco.preprocessor;

import at.jku.isse.gitecco.cdt.TreeFeature;
import at.jku.isse.gitecco.conditionparser.ParsedCondition;

import java.util.List;

/**
 * Class for pseudo preprocessing c/c++ files.
 * With the provided methods it is possible the cut the unchanged features
 */
public class FeaturePreprocessor {

    public void preprocess(TreeFeature tree, String file) {
        List<TreeFeature> changed = tree.getChangedAsList();
        String command = "coan source ";
        for (TreeFeature tf : changed) {
            for (ParsedCondition pc : tf.getConditions()) {
                int value = (int)pc.getDefinition();
                if(!command.contains("-D" + pc.getName() + "=" + value)) {
                    command += "-D" + pc.getName() + "=" + value +  " ";
                }
            }
        }
        command += "-m -ge -P -r " + file;
        System.out.println(command);
    }
}
