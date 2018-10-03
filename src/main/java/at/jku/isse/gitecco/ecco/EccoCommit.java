package at.jku.isse.gitecco.ecco;

import at.jku.isse.gitecco.cdt.Feature;
import at.jku.isse.gitecco.cdt.TreeFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of EccoCommand for an ecco commit.
 * Contains features for committing.
 * The getCommandMsg gets the command for committing, which will later be passed on to the ecco tool.
 */
public class EccoCommit implements EccoCommand {

    private final List<TreeFeature> features;

    /**
     * Creates a new EccoCommit with the passed features.
     *
     * @param tf
     */
    public EccoCommit(List<TreeFeature> tf) {
        features = new ArrayList<TreeFeature>(tf);
    }

    /**
     * Allows to add features to the commit after it was initialized by the constructor.
     *
     * @param tf
     */
    public void addFeature(TreeFeature tf) {
        features.add(tf);
    }


    @Override
    public String getCommandMsg() {
        String retFeatures = "";
        String buf = "";
        for (TreeFeature feature : features) {
            buf = "";
            for(String name : feature.getName()) {
                buf += name + "' ";
            }
            if (!retFeatures.contains(buf)) {
                retFeatures += buf;
            }
            TreeFeature t = feature.getParent();
            while(t != null) {
                for(String name : t.getName()) {
                    if (!retFeatures.contains(name)) retFeatures += name + " ";
                }
                t = t.getParent();
            }
        }

        retFeatures = retFeatures.equals("") ? "BASE" : retFeatures;
        return "ecco commit "+retFeatures;
    }
}
