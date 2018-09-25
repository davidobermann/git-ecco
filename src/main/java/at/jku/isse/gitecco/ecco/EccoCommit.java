package at.jku.isse.gitecco.ecco;

import at.jku.isse.gitecco.cdt.Feature;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of EccoCommand for an ecco commit.
 * Contains features for committing.
 * The getCommandMsg gets the command for committing, which will later be passed on to the ecco tool.
 */
public class EccoCommit implements EccoCommand {

    private final List<Feature> features;

    /**
     * Creates a new EccoCommit with the passed features.
     *
     * @param f
     */
    public EccoCommit(Feature[] f) {
        features = new ArrayList<Feature>();
        for (Feature feature : f) {
            features.add(feature);
        }
    }

    /**
     * Allows to add features to the commit after it was initialized by the constructor.
     *
     * @param f
     */
    public void addFeature(Feature f) {
        features.add(f);
    }


    @Override
    public String getCommandMsg() {
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
