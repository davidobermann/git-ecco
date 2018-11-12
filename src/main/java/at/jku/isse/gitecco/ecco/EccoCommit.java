package at.jku.isse.gitecco.ecco;

import at.jku.isse.gitecco.cdt.Feature;
import at.jku.isse.gitecco.cdt.FeatureType;
import at.jku.isse.gitecco.cdt.TreeFeature;
import at.jku.isse.gitecco.conditionparser.ParsedCondition;

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
     * Creates a new EccoCommit.
     * If the parameter is true --> mark BASE as changed
     * If the parameter is false --> nothing changed
     *
     * @param baseChanged
     */
    public EccoCommit(boolean baseChanged) {
        features = new ArrayList<TreeFeature>();
        features.add(new TreeFeature(new Feature(0, FeatureType.IF,new ParsedCondition("BASE",1))));
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
            for(ParsedCondition pc : feature.getConditions()) {
                buf += pc.getName() + "' ";
            }
            if (!retFeatures.contains(buf)) {
                retFeatures += buf;
            }
            TreeFeature t = feature.getParent();
            while(t != null) {
                for(ParsedCondition pc : t.getConditions()) {
                    if (!retFeatures.contains(pc.getName())) retFeatures += pc.getName() + " ";
                }
                t = t.getParent();
            }
        }

        retFeatures = retFeatures.equals("") ? "BASE" : retFeatures;
        return "ecco commit "+retFeatures;
    }
}
