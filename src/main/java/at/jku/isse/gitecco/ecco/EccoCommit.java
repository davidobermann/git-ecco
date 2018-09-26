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
    public EccoCommit(TreeFeature tf) {
        features = new ArrayList<TreeFeature>();
        //TODO: get all the changed features in the tree and add them to the list.
    }

    /**
     * Allows to add features to the commit after it was initialized by the constructor.
     *
     * @param f
     */
    public void addFeature(Feature f) {
        //TODO: create a method to add ned features to this commit command
        //features.add(f);
    }


    @Override
    public String getCommandMsg() {

        //TODO: take every changed feature from the list and get the name and all parent names.

        String retFeatures = "";
        for (Feature feature : features) {
            if (!retFeatures.contains(feature.getNames()+"'")) {
                retFeatures += feature.getNames()+"' ";
            }
        }
        if (retFeatures == "") {
            retFeatures = "BASE'";
        } else if (!retFeatures.contains("BASE'")) {
            retFeatures += "BASE";
        }

        return "ecco commit "+retFeatures;
    }
}
